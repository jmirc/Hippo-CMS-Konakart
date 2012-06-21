package org.onehippo.forge.konakart.cms.plugins.products;

/**
 * Copyright 2001-2010 Hippo (www.hippo.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.model.JcrItemModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.IBrowseService;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.api.*;
import org.hippoecm.repository.standardworkflow.DefaultWorkflow;
import org.hippoecm.repository.standardworkflow.FolderWorkflow;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ProductWorkflowPlugin extends RenderPlugin<WorkflowDescriptor> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ProductWorkflowPlugin.class);

    private static final ResourceReference CSS = new CompressedResourceReference(ProductWorkflowPlugin.class, "ProductWorkflowPlugin.css");

    private StdWorkflow copyAction;

    private Node currentDocument;

    /**
     * This class creates a link on the dashboard. The link opens a dialog that allow the user to quickly create a
     * document. The location of the document can be configured.
     */
    public ProductWorkflowPlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);


        add(copyAction = new StdWorkflow("copy", new StringResourceModel("copy-label", this, null).getString()) {

            @Override
            protected ResourceReference getIcon() {
                return new ResourceReference(getClass(), "copy-16.png");
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                return super.createRequestDialog();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        modelChanged();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onModelChanged() {
        super.onModelChanged();

        WorkflowDescriptorModel model = (WorkflowDescriptorModel) ProductWorkflowPlugin.this.getDefaultModel();
        if (model != null) {
            try {
                currentDocument = model.getNode();
            } catch (RepositoryException e) {
                log.error("Error getting document node from WorkflowDescriptorModel", e);
            }
        }
    }

    /**
     * The dialog that opens after the user has clicked the dashboard link.
     */
    protected class Dialog extends AbstractDialog<Object> {

        private static final String DIALOG_NAME_LABEL = "name-label";

        private final IPluginContext context;
        private final IPluginConfig config;
        private final String documentType = KKCndConstants.PRODUCT_DOC_TYPE;

        private String documentName;
        private String list;
        private Date date;

        /**
         * @param context plugin context
         * @param config  plugin config
         * @param parent  parent component
         */
        public Dialog(final IPluginContext context, final IPluginConfig config, Component parent) {
            this.context = context;
            this.config = config;

            feedback = new FeedbackPanel("feedback");
            replace(feedback);
            feedback.setOutputMarkupId(true);

            documentName = "";
            list = "";
            date = new Date();

            // add name text field
            Label nameLabel = getLabel(DIALOG_NAME_LABEL, config);
            add(nameLabel);
            IModel<String> nameModel = new PropertyModel<String>(this, "documentName");
            TextField<String> nameField = new TextField<String>("name", nameModel);
            nameField.setRequired(true);
            final StringResourceModel errorMsgModel = new StringResourceModel("invalid.name", this, null);
            nameField.add(new IValidator<String>() {
                public void validate(final IValidatable<String> strValue) {
                    String value = strValue.getValue();
                    if (!isValidName(value)) {
                        strValue.error(new IValidationError() {
                            public String getErrorMessage(final IErrorMessageSource messageSource) {
                                return errorMsgModel.getString();
                            }
                        });
                    }
                }
            });
            nameField.setLabel(new StringResourceModel(DIALOG_NAME_LABEL, this, null));
            add(nameField);
        }

        /**
         * Get a label from the plugin config, or from the Dialog properties file.
         *
         * @param labelKey the key under which the label is stored
         * @param config   the config of the plugin
         * @return a wicket Label
         */
        private Label getLabel(final String labelKey, final IPluginConfig config) {
            final IPluginConfig localeConfig = config.getPluginConfig(getSession().getLocale().toString());
            if (localeConfig != null) {
                final String label = localeConfig.getString(labelKey);
                if (StringUtils.isNotBlank(label)) {
                    return new Label(labelKey, label);
                }
            }
            return new Label(labelKey, new StringResourceModel(labelKey, this, null));
        }

        @Override
        protected void onOk() {
            Session session = ((UserSession) getSession()).getJcrSession();
            HippoWorkspace workspace = (HippoWorkspace) session.getWorkspace();
//            try {
//                WorkflowManager workflowMgr = workspace.getWorkflowManager();
//
//                // get the folder node
//                HippoNode folderNode = (HippoNode) session.getItem(baseFolder);
//
//                // get the folder node's workflow
//                Workflow workflow = workflowMgr.getWorkflow("internal", folderNode);
//
//                if (workflow instanceof FolderWorkflow) {
//                    FolderWorkflow fw = (FolderWorkflow) workflow;
//
//                    // create the new document
//                    String encodedDocumentName = getNodeNameCodec().encode(documentName);
//                    Map<String, String> arguments = new TreeMap<String, String>();
//                    arguments.put("name", encodedDocumentName);
//                    log.debug("Query used: " + query);
//                    String path = fw.add(query, documentType, arguments);
//
//                    Node document = folderNode.getNode(encodedDocumentName);
//                    JcrNodeModel nodeModel = new JcrNodeModel(new JcrItemModel(path));
//                    select(nodeModel);
//
//                    // add the not-encoded document name as translation
//                    if (!documentName.equals(encodedDocumentName)) {
//                        DefaultWorkflow defaultWorkflow = (DefaultWorkflow) workflowMgr.getWorkflow("core", nodeModel.getNode());
//                        if (defaultWorkflow != null) {
//                            defaultWorkflow.localizeName(documentName);
//                        }
//                    }
//                }
//            } catch (RepositoryException e) {
//                log.error("Error occurred while creating new document: "
//                        + e.getClass().getName() + ": " + e.getMessage());
//            } catch (RemoteException e) {
//                log.error("Error occurred while creating new document: "
//                        + e.getClass().getName() + ": " + e.getMessage());
//            } catch (WorkflowException e) {
//                log.error("Error occurred while creating new document: "
//                        + e.getClass().getName() + ": " + e.getMessage());
//            }

        }

        @Override
        protected void onValidate() {
            super.onValidate();
        }

        private IPluginContext getPluginContext() {
            return context;
        }

        protected void select(JcrNodeModel nodeModel) {
            String browserId = config.getString(IBrowseService.BROWSER_ID);
            @SuppressWarnings("unchecked")
            IBrowseService<JcrNodeModel> browser = getPluginContext().getService(browserId, IBrowseService.class);
            if (browser != null) {
                browser.browse(nodeModel);
            } else {
                log.warn("no browser service found");
            }
        }

        @Override
        public IModel getTitle() {
            return new StringResourceModel("title", this, null);
        }
    }

    /**
     * Determine whether the a document name is valid.
     *
     * @param value the document name
     * @return whether the name is a valid JCR nodename
     */
    protected static boolean isValidName(final String value) {
        if (!value.trim().equals(value)) {
            return false;
        }
        if (".".equals(value) || "..".equals(value)) {
            return false;
        }
        return value.matches("[^\\[\\]\\|/:\\}\\{]+");
    }

    protected StringCodec getNodeNameCodec() {
        return new StringCodecFactory.UriEncoding();
    }


//
//
//    /**
//     * Get or create folder for classificationType.LIST.
//     *
//     * @param parentNode
//     * @param list
//     * @return
//     * @throws java.rmi.RemoteException
//     * @throws javax.jcr.RepositoryException
//     * @throws org.hippoecm.repository.api.WorkflowException
//     *
//     */
//    protected HippoNode listFolder(HippoNode parentNode, String list) throws RemoteException, RepositoryException, WorkflowException {
//        String listEncoded = getNodeNameCodec().encode(list);
//        HippoNode resultParentNode = parentNode;
//        if (resultParentNode.hasNode(listEncoded)) {
//            resultParentNode = (HippoNode) resultParentNode.getNode(listEncoded);
//        } else {
//            final String listEncodedLowerCase = listEncoded.toLowerCase(getLocale());
//            if (resultParentNode.hasNode(listEncodedLowerCase)) {
//                resultParentNode = (HippoNode) resultParentNode.getNode(listEncodedLowerCase);
//            } else {
//                resultParentNode = createFolder(resultParentNode, listEncoded);
//            }
//        }
//        return resultParentNode;
//    }
//
//    /**
//     * Get or create folder(s) for classificationType.DATE.
//     *
//     * @param parentNode
//     * @param date
//     * @return
//     * @throws java.rmi.RemoteException
//     * @throws javax.jcr.RepositoryException
//     * @throws org.hippoecm.repository.api.WorkflowException
//     *
//     */
//    protected HippoNode createDateFolders(HippoNode parentNode, Date date) throws RemoteException, RepositoryException, WorkflowException {
//        String year = new SimpleDateFormat("yyyy").format(date);
//        HippoNode resultParentNode = parentNode;
//        if (resultParentNode.hasNode(year)) {
//            resultParentNode = (HippoNode) resultParentNode.getNode(year);
//        } else {
//            resultParentNode = createFolder(resultParentNode, year);
//        }
//
//        String month = new SimpleDateFormat("MM").format(date);
//        if (resultParentNode.hasNode(month)) {
//            resultParentNode = (HippoNode) resultParentNode.getNode(month);
//        } else {
//            resultParentNode = createFolder(resultParentNode, month);
//        }
//
//        return resultParentNode;
//    }
//
//    protected HippoNode createFolder(HippoNode parentNode, String name) throws RepositoryException, RemoteException, WorkflowException {
//        Session session = ((UserSession) getSession()).getJcrSession();
//        HippoWorkspace workspace = (HippoWorkspace) session.getWorkspace();
//        WorkflowManager workflowMgr = workspace.getWorkflowManager();
//
//        // get the folder node's workflow
//        Workflow workflow = workflowMgr.getWorkflow("internal", parentNode);
//
//        if (workflow instanceof FolderWorkflow) {
//            FolderWorkflow fw = (FolderWorkflow) workflow;
//
//            // create the new folder
//            String category = "new-folder";
//            NodeType[] mixinNodeTypes = parentNode.getMixinNodeTypes();
//            for (int i = 0; i < mixinNodeTypes.length; i++) {
//                NodeType mixinNodeType = mixinNodeTypes[i];
//                if (mixinNodeType.getName().equals("hippotranslation:translated")) {
//                    category = "new-translated-folder";
//                    break;
//                }
//            }
//            fw.add(category, "hippostd:folder", name);
//
//            HippoNode newFolder = (HippoNode) parentNode.getNode(name);
//
//            // give the new folder the same folder types as its parent
//            Property parentFolderType = parentNode.getProperty("hippostd:foldertype");
//            newFolder.setProperty("hippostd:foldertype", parentFolderType.getValues());
//
//            // try to reorder the folder
//            reorderFolder(fw, parentNode);
//
//            return newFolder;
//        } else {
//            throw new WorkflowException("Workflow is not an instance of FolderWorkflow");
//        }
//
//    }
//
//    protected void reorderFolder(final FolderWorkflow folderWorkflow, final HippoNode parentNode) {
//        // intentional stub
//    }
//
//    private static class ListChoiceRenderer implements IChoiceRenderer<Object> {
//        private static final long serialVersionUID = 1L;
//        private final ValueList list;
//
//        public ListChoiceRenderer(ValueList list) {
//            this.list = list;
//        }
//
//        public Object getDisplayValue(Object object) {
//            return list.getLabel(object);
//        }
//
//        public String getIdValue(Object object, int index) {
//            return list.getKey(object);
//        }
//
//    }

}