package org.onehippo.forge.konakart.cms.replication.utils;

import org.hippoecm.repository.api.*;
import org.hippoecm.repository.standardworkflow.DefaultWorkflow;
import org.hippoecm.repository.standardworkflow.FolderWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.rmi.RemoteException;
import java.util.UUID;

public class NodeImagesHelper {

    public static final String PUBLISHED_STATE = "published";
    public static final String UNPUBLISHED_STATE = "unpublished";

    public static final Logger log = LoggerFactory.getLogger(NodeImagesHelper.class);


    /**
     * Hippo Repository specific predefined folder node type name
     */
    protected String folderNodeTypeName = "image gallery";

    /**
     * The workflow category name to get a folder workflow. We use threepane as this is the same as the CMS uses
     */
    protected String folderNodeWorkflowCategory = "threepane";

    /**
     * The workflow category name to add a new document.
     */
    protected String documentAdditionWorkflowCategory = "new-image-folder";

    /**
     * The workflow category name to add a new folder.
     */
    protected String galleryType = "hippogallery:imageset";

    /**
     * The workflow category name to localize the new document
     */
    protected String defaultWorkflowCategory = "core";

    protected Session session;


    public NodeImagesHelper(Session session) {
        this.session = session;
    }
    
    
    public Node createMissingFolders(String absPath) throws Exception {
        String[] folderNames = absPath.split("/");

        Node rootNode = session.getRootNode();
        Node curNode = rootNode;
        String folderNodePath;

        for (String folderName : folderNames) {
            String folderNodeName = Codecs.encodeNode(folderName);

            if (!"".equals(folderNodeName)) {
                if (curNode.equals(rootNode)) {
                    folderNodePath = "/" + folderNodeName;
                } else {
                    folderNodePath = curNode.getPath() + "/" + folderNodeName;
                }

                if (!session.itemExists(folderNodePath)) {
                    curNode = session.getNode(createNodeByWorkflow(curNode, folderNodeTypeName, folderName));

                    if (!curNode.hasProperty("hippostd:gallerytype")) {
                        curNode.setProperty("hippostd:gallerytype", galleryType);
                    }
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

                String nodeName = Codecs.encodeNode(name);
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

    public Node createGalleryItem(Node parentNode, String filename) throws RepositoryException {

        String nodeName =  Codecs.encodeNode(filename);

        if (parentNode.hasNode(nodeName)) {
            Node handleNode = parentNode.getNode(nodeName);

            if (handleNode.hasNode(nodeName)) {
                return handleNode.getNode(nodeName);
            }

            return null;
        }

        // Create the handle
        Node handle = parentNode.addNode(filename, "hippo:handle");
        handle.addMixin("hippo:hardhandle");
        handle.addMixin("hippo:translated");

        // Create the image folder
        Node childNode = handle.addNode(filename, "hippogallery:imageset");

        // Add mixin
        childNode.addMixin("hippo:harddocument");

        // Add extra definitions
        childNode.setProperty("hippo:availability", new String[]{"live", "preview"});
        childNode.setProperty("hippogallery:filename", filename);

        return childNode;

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
            if (workspaceClassloader.equals(currentClassloader)) {
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
            if (workspaceClassloader.equals(currentClassloader)) {
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
