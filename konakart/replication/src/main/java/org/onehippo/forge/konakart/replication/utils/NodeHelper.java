package org.onehippo.forge.konakart.replication.utils;

import com.konakart.app.Product;
import org.hippoecm.repository.api.*;
import org.hippoecm.repository.standardworkflow.DefaultWorkflow;
import org.hippoecm.repository.standardworkflow.FolderWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NodeHelper {

    public static final String PUBLISHED_STATE = "published";
    public static final String UNPUBLISHED_STATE = "unpublished";

    public static final Logger log = LoggerFactory.getLogger(NodeHelper.class);


    /**
     * Hippo Repository specific predefined folder node type name
     */
    protected String folderNodeTypeName = "hippostd:folder";

    /**
     * The workflow category name to get a folder workflow. We use threepane as this is the same as the CMS uses
     */
    protected String folderNodeWorkflowCategory = "threepane";

    /**
     * The workflow category name to add a new document.
     */
    protected String documentAdditionWorkflowCategory = "new-document";

    /**
     * The workflow category name to add a new folder.
     */
    protected String folderAdditionWorkflowCategory = "new-folder";

    /**
     * The workflow category name to localize the new document
     */
    protected String defaultWorkflowCategory = "core";

    /**
     * The codec which is used for the node names
     */
    public StringCodec uriEncoding = new StringCodecFactory.UriEncoding();

    protected Session session;


    public NodeHelper(Session session) {
        this.session = session;
    }


    /**
     * Creates content node(s) with the specified node type at the specified absolute path.
     * <p/>
     * The absolute path could be regarded differently according to physical implementations.
     * For example, an implementation can regard the path as a simple one to add a simple JCR node.
     * On the other hand, a sophisticated implementation can regard the path as an input for
     * a workflow-enabled document/folder path.
     * </P>
     * <p/>
     * If <CODE>autoCreateFolders</CODE> is true, then folders will be automatically created.
     * </P>
     *
     * @param absPath           the absolute node path
     * @param nodeTypeName      the node type name of the content object
     * @param name              the content node name
     * @param autoCreateFolders the flag to add folders
     * @return the absolute path of the created node
     */
    public String createAndReturn(final String absPath, final String nodeTypeName, final String name, final boolean autoCreateFolders) throws Exception {
        final Node parentFolderNode;
        if (!session.itemExists(absPath)) {
            if (!autoCreateFolders) {
                throw new Exception("The folder node is not found on the path: " + absPath);
            } else {
                parentFolderNode = createMissingFolders(absPath);
            }
        } else {
            parentFolderNode = session.getNode(absPath);
        }

        return createNodeByWorkflow(parentFolderNode, nodeTypeName, name);
    }

    public Node createMissingFolders(String absPath) throws Exception {
        String[] folderNames = absPath.split("/");

        Node rootNode = session.getRootNode();
        Node curNode = rootNode;
        String folderNodePath;

        for (String folderName : folderNames) {
            String folderNodeName = uriEncoding.encode(folderName);

            if (!"".equals(folderNodeName)) {
                if (curNode == rootNode) {
                    folderNodePath = "/" + folderNodeName;
                } else {
                    folderNodePath = curNode.getPath() + "/" + folderNodeName;
                }

                if (!session.itemExists(folderNodePath)) {
                    curNode = session.getNode(createNodeByWorkflow(curNode, folderNodeTypeName, folderName));
                } else {
                    curNode = curNode.getNode(folderNodeName);
                }

                if (curNode.isNodeType(HippoNodeType.NT_FACETSELECT) || curNode.isNodeType(HippoNodeType.NT_MIRROR)) {
                    String docbaseUuid = curNode.getProperty("hippo:docbase").getString();
                    // check whether docbaseUuid is a valid uuid, otherwise a runtime IllegalArgumentException is thrown
                    try {
                        UUID.fromString(docbaseUuid);
                    } catch (IllegalArgumentException e) {
                        throw new Exception("hippo:docbase in mirror does not contain a valid uuid", e);
                    }
                    // this is always the canonical
                    curNode = session.getNodeByIdentifier(docbaseUuid);
                } else {
                    curNode = getCanonicalNode(curNode);
                }
            }
        }

        return curNode;
    }

    @SuppressWarnings("rawtypes")
    protected String createNodeByWorkflow(Node folderNode, String nodeTypeName, String name) throws Exception {
        try {
            folderNode = getCanonicalNode(folderNode);
            Workflow wf = getWorkflow(folderNodeWorkflowCategory, folderNode);

            if (wf instanceof FolderWorkflow) {
                FolderWorkflow fwf = (FolderWorkflow) wf;

                String category = documentAdditionWorkflowCategory;


                if (nodeTypeName.equals(folderNodeTypeName)) {
                    category = folderAdditionWorkflowCategory;

                    // now check if there is some more specific workflow for hippostd:folder
                    if (fwf.hints() != null && fwf.hints().get("prototypes") != null) {
                        Object protypesMap = fwf.hints().get("prototypes");
                        if (protypesMap instanceof Map) {
                            for (Object o : ((Map) protypesMap).entrySet()) {
                                Map.Entry entry = (Map.Entry) o;
                                if (entry.getKey() instanceof String && entry.getValue() instanceof Set) {
                                    if (((Set) entry.getValue()).contains(folderNodeTypeName)) {
                                        // we found possibly a more specific workflow for folderNodeTypeName. Use the key as category
                                        category = (String) entry.getKey();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                String nodeName = uriEncoding.encode(name);
                String added = fwf.add(category, nodeTypeName, nodeName);
                if (added == null) {
                    throw new Exception("Failed to add document/folder for type '" + nodeTypeName
                            + "'. Make sure there is a prototype.");
                }
                Item addedDocumentVariant = folderNode.getSession().getItem(added);
                if (addedDocumentVariant instanceof Node && !nodeName.equals(name)) {
                    DefaultWorkflow defaultWorkflow = (DefaultWorkflow) getWorkflow(defaultWorkflowCategory, (Node) addedDocumentVariant);
                    defaultWorkflow.localizeName(name);
                }
                return added;
            } else {
                throw new Exception("Can't add folder " + name + " [" + nodeTypeName + "] in the folder " + folderNode.getPath() + ", because there is no FolderWorkflow possible on the folder node: " + wf);
            }
        } catch (RepositoryException e) {
            throw new Exception(e);
        } catch (RemoteException e) {
            throw new Exception(e);
        } catch (WorkflowException e) {
            throw new Exception(e);
        }
    }

    public Node createDocument(Node parentNode, Product product, String docType, String ownerId, String locale) throws RepositoryException {

        // Encode the name to be able to add name with special characters
        String encodingName = uriEncoding.encode(product.getName());

        if (parentNode.hasNode(encodingName)) {
            Node handleNode = parentNode.getNode(encodingName);

            if (handleNode.hasNode(encodingName)) {
                Node docName = handleNode.getNode(encodingName);

                docName.getProperty("hippostdpubwf:lastModificationDate").setValue(new GregorianCalendar());

                return docName;
            }

            return null;
        }

        // Create the handle
        Node handle = parentNode.addNode(encodingName, "hippo:handle");
        handle.addMixin("hippo:hardhandle");
        handle.addMixin("hippo:translated");

        // Add translation node. This node is used to manager special name
        Node translation = handle.addNode("hippo:translation", "hippo:translation");
        translation.setProperty("hippo:language", "");
        translation.setProperty("hippo:message", product.getName());

        // Create the user
        Node childNode = handle.addNode(encodingName, docType);

        // Add mixin
        childNode.addMixin("hippo:harddocument");
        childNode.addMixin("hippotranslation:translated");

        // Add extra definitions
        childNode.setProperty("hippo:availability", new String[]{"live", "preview"});
        childNode.setProperty("hippotranslation:id", UUID.randomUUID().toString());
        childNode.setProperty("hippotranslation:locale", locale);
        childNode.setProperty("hippostdpubwf:lastModifiedBy", "admin");
        childNode.setProperty("hippostd:holder", "admin");
        childNode.setProperty("hippostdpubwf:lastModificationDate", new GregorianCalendar());
        childNode.setProperty("hippostdpubwf:creationDate", new GregorianCalendar());
        childNode.setProperty("hippostdpubwf:publicationDate", new GregorianCalendar());
        childNode.setProperty("hippostdpubwf:createdBy", ownerId);

        return childNode;

    }

    /**
     * Update the hippostd state
     * @param state the state of the document
     */
    public void updateState(Node node, String state) throws RepositoryException {
        node.setProperty("hippostd:state", state);
    }



    /**
     * Saves all pending changes.
     *
     * @throws Exception .
     */
    public void save() throws Exception {
        try {
            session.save();
            // also do a refresh, because it is possible that through workflow another jcr session made the changes, and that the current
            // has no changes, hence a session.save() does not trigger a refresh
            session.refresh(false);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    /**
     * Invokes {@link javax.jcr.Session#refresh(boolean)} with <CODE>false</CODE> parameter.
     *
     * @throws Exception .
     */
    public void refresh() throws Exception {
        refresh(false);
    }

    /**
     * Invokes {@link javax.jcr.Session#refresh(boolean)}.
     *
     * @param keepChanges .
     * @throws Exception .
     */
    public void refresh(boolean keepChanges) throws Exception {
        try {
            session.refresh(keepChanges);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    public Workflow getWorkflow(String category, Node node) throws RepositoryException {
        Workspace workspace = session.getWorkspace();

        ClassLoader workspaceClassloader = workspace.getClass().getClassLoader();
        ClassLoader currentClassloader = Thread.currentThread().getContextClassLoader();

        try {
            if (workspaceClassloader != currentClassloader) {
                Thread.currentThread().setContextClassLoader(workspaceClassloader);
            }

            WorkflowManager wfm = ((HippoWorkspace) workspace).getWorkflowManager();
            return wfm.getWorkflow(category, node);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // other exception which are not handled properly in the repository (we cannot do better here then just log them)
            if (log.isDebugEnabled()) {
                log.warn("Exception in workflow", e);
            } else {
                log.warn("Exception in workflow: {}", e.toString());
            }
        } finally {
            if (workspaceClassloader != currentClassloader) {
                Thread.currentThread().setContextClassLoader(currentClassloader);
            }
        }

        return null;
    }


    private Node getCanonicalNode(Node folderNode) {
        if (folderNode instanceof HippoNode) {
            HippoNode hnode = (HippoNode) folderNode;
            try {
                Node canonical = hnode.getCanonicalNode();
                if (canonical == null) {
                    log.debug("Cannot get canonical node for '{}'. This means there is no phyiscal equivalence of the " +
                            "virtual node. Return null", folderNode.getPath());
                }
                return canonical;
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        return folderNode;
    }
}
