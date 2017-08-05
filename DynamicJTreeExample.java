package com.floppypanda.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DynamicJTreeExample {
	//The tree displays the current directory by default.
	final static Path currentDirectory = Paths.get(System.getProperty("user.dir"));
	
	public static void main(String[] args) {
		//Creating and configuring Swing components.
		JFrame frame = new JFrame();
		frame.setTitle("Dynamic JTree Example");
		frame.setBounds(50, 50, 500, 500);
		frame.setLayout(new BorderLayout());
		JTree tree = new JTree();
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		JTextField textField = new JTextField();
		textField.setText(currentDirectory.toString());
		JButton fileChooseButton = new JButton();
		fileChooseButton.setText("...");
		fileChooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//Opening file chooser dialog for selecting directory.
				int res = fc.showOpenDialog(frame);
				if (res == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					textField.setText(selectedFile.getAbsolutePath().toString());
					//Updating the tree to display contents of new directory.
					DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
					DefaultMutableTreeNode root = new DefaultMutableTreeNode(selectedFile.getName());
					model.setRoot(root);
			        generateNodes(root, selectedFile);	
			        model.reload();
				}
			}
		});
		
		//Initializing the JTree to display the current directory.
		initializeJTree(tree);
		frame.add(tree, BorderLayout.CENTER);
		bottomPanel.add(textField, BorderLayout.CENTER);
		bottomPanel.add(fileChooseButton, BorderLayout.LINE_END);
		frame.add(bottomPanel, BorderLayout.PAGE_END);
		frame.setVisible(true);

	}
	
	/**
	 * Initializes the JTree to display files in the current directory.
	 * @param tree
	 */
	private static void initializeJTree(JTree tree) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentDirectory.getFileName());
		model.setRoot(root);
        generateNodes(root, new File(currentDirectory.toString()));	
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
				//There is no need to alter child nodes following a collapse.
			}
			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				//Removing the dummy node.
				node.remove(0);
				//Generating child nodes.
				generateNodes(node, (File) node.getUserObject());
				model.reload(node);
			}			
		});
		model.reload(root);
	}
	
	/**
	 * Generates the child nodes of a given parent node.
	 * @param parent The parent node for which children will be generated.
	 * @param parentFile The file corresponding to the parent, must be a directory.
	 */
	private static void generateNodes(DefaultMutableTreeNode parent, File parentFile) {
		//Generating children tree nodes.
		File[] childFiles = parentFile.listFiles();
		for (File childFile : childFiles) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(childFile.getName());
			//Associating the child node with the corresponding file.
			child.setUserObject(childFile);
			
			//Adding a dummy node to nodes corresponding to directories.
			//This ensures that the node will have an expansion button.
			if (childFile.isDirectory()) {
				DefaultMutableTreeNode dummyNode = new DefaultMutableTreeNode();
				child.add(dummyNode);
			}			
			//Adding the child node to parent node.
			parent.add(child);
		}
	}
}
