/* @version 1.0 | 20171019
 * @license: none (public login)
 */
function AddTreeView(container, id) {
    var tree;
    if (!window[id]) {
        if (container) {
            tree = new TreeView(id);
            container.appendChild(tree);
        }
    }
    else {
        tree = window[id];
    }
    return tree;
}


var TreeView = function (id) {
    var tree = window[id];
    if (!tree) {
        tree = document.createElement("ul");
        tree.className = "nav nav-list";
        tree.id = id;
        tree.AllNodes = {};
        tree.Nodes = [];
        tree.DefaultState = "Collapse";
        tree.MultiSelect = false;
        tree.SelectedNodes = [];
        tree.CheckedNodes = [];

        tree.ClearNodes = function () {
            for (var n in tree.Nodes) {
                tree.removeChild(tree.Nodes[n]);
            }
        }

        tree.AddNodes = function (id, text, pid, icon, url, checked) {
            var node = document.createElement("li");
            node.id = id;
			if(pid==0)
               node.className = "active";
            node.Nodes = new Array();
            node.Checked = false;
            node.Text = text;
            node.State = tree.DefaultState;
            node.Tree = tree;
            var mark = document.createElement("i");
            mark.Node = node;

//            if (icon)
//                mark.className = icon;
if(pid==0)
			mark.className="icon-file-alt";
else
  mark.className="icon-double-angle-right";
//            else {
//                if (pid == "" || pid == null)
//                    mark.className = "icon-tint";
//                else
//                    mark.className = "icon-caret-right";
//            }

            mark.ChangeMark = function () {
                //                if (this.Node.Index == this.Node.Parent.Nodes.length - 1) {
                //                    if (this.Node.Nodes.length == 0) {
                //                        this.className = "TreeNodeLeafLast";
                //                    }
                //                    else {
                //                        if (this.Node.State == "Expand") {
                //                            this.className = "TreeNodeOpenedLast";
                //                            this.Node.ChildNodes.className = 'TreeGroupLast';
                //                        }
                //                        else {
                //                            this.className = "TreeNodeClosedLast";
                //                            this.Node.ChildNodes.className = 'TreeGroupLast TreeGroupClosed';
                //                        }
                //                    }
                //                }
                //                else {
                //                    if (this.Node.Nodes.length == 0) {
                //                        this.className = "TreeNodeLeaf";
                //                    }
                //                    else {
                //                        if (this.Node.State == "Expand") {
                //                            this.className = "TreeNodeOpened";
                //                            this.Node.ChildNodes.className = 'TreeGroup';
                //                        }
                //                        else {
                //                            this.className = "TreeNodeClosed";
                //                            this.Node.ChildNodes.className = 'TreeGroup TreeGroupClosed';
                //                        }
                //                    }
                //                }
            }
            node.Mark = mark;

            mark.onclick = function () {
             node.Toggle();
            }
            // node.appendChild(mark);

            var lable = document.createElement("a")
            lable.Node = node;
            node.Label = lable;
			if(pid==0)
               lable.className = "dropdown-toggle";
            if (url && url != "" && url !=null  && url !=undefined) {
				lable.setAttribute("href", "javascript:addTab('"+node.Text+"','"+url+"');");
                //lable.setAttribute("href", url);
                //lable.setAttribute("target", "main");
            }
            lable.appendChild(mark);


            if (tree.NodeClick) {
                lable.onclick = function () {
                    this.Node.Select();
                    tree.NodeClick(this.Node);
                }
            }
            if (tree.NodeCommand) {
                lable.ondblclick = function () {
                    tree.NodeCommand(this.Node);
                }
            }
			if(pid==0)
		    {
              var caption = document.createElement("span");
			  caption.className="menu-text";
			  caption.innerHTML = text;
			  node.Cpation = caption;
		    }
//			else
//			   caption=text;
            
            


            if (checked != undefined) {
                var chk = document.createElement("input");
                chk.type = "checkbox";
                chk.className = "NodeCheckBox";
                lable.appendChild(chk);
                chk.checked = checked ? "checked" : "";
                node.CheckBox = chk;
            }

if(pid==0)
            lable.appendChild(caption);
			else
			lable.text=text;

            node.appendChild(lable);

            node.setText = function (txt) {
                node.Caption.innerHTML = txt;
            }

            var pnode;
            if (pid) {
                pnode = tree.AllNodes[pid];
            }
            if (pnode) {
                pnode.Nodes.push(node);
                tree.AllNodes[id] = node;
                var cnodes = pnode.ChildNodes;
                if (!cnodes) {
                    cnodes = document.createElement("ul");
                    cnodes.className = "submenu";
                    pnode.appendChild(cnodes);
                    pnode.ChildNodes = cnodes;
                }
                cnodes.appendChild(node);
                node.Index = pnode.Nodes.length - 1;
                node.Parent = pnode;
                node.Level = pnode.Level + 1;

                if (node.Index > 0) {
                    pnode.Nodes[node.Index - 1].Mark.ChangeMark();
                }
                pnode.Mark.ChangeMark();
            }
            else {
                tree.AllNodes[id] = node;
                tree.Nodes.push(node);
                node.Parent = tree;
                tree.appendChild(node);
                node.Level = 1;
                node.Index = tree.Nodes.length - 1;
                if (node.Index > 0) {
                    tree.Nodes[node.Index - 1].Mark.ChangeMark();
                }
            }
            if (tree.OnNodeAdded) {
                tree.OnNodeAdded(node);
            }

            node.Toggle = function () {
                if (this.Nodes.length > 0) {
                    if (this.State == "Expand") {
                        this.State = "Collapse";
                        this.Mark.ChangeMark();
                    }
                    else {
                        this.State = "Expand";
                        this.Mark.ChangeMark();
                    }
                }
            }
            node.Select = function () {
                if (!this.Tree.MultiSelect) {
                    if (this.Tree.SelectedNodes.length > 0) {
                        this.Tree.SelectedNodes[0].UnSelect();
                    }
                }
                this.Label.className = "TreeNodeSelected"
                this.Tree.SelectedNodes.push(this);
            }
            node.UnSelect = function () {
                if (this.Tree.SelectedNodes.length > 0) {
                    for (var i = 0; i < this.Tree.SelectedNodes.length; i++) {
                        if (this.Tree.SelectedNodes[i] == this) {
                            this.Tree.SelectedNodes.splice(i, 1);
                            break;
                        }
                    }
                    this.Label.className = ""
                }
            }
            node.Check = function () {
                if (this.CheckBox) {
                    this.CheckBox.checked = "checked";
                    this.Label.className = "TreeNodeChecked"
                    this.Tree.CheckedNodes.push(this);
                }
            }
            node.UnCheck = function () {
                if (this.CheckBox) {
                    if (this.Tree.CheckedNodes.lenght > 0) {
                        for (var i = 0; i < this.Tree.CheckedNodes.length; i++) {
                            if (this.Tree.CheckedNodes[i] == this) {
                                this.Tree.CheckedNodes.splice(i, 1);
                                break;
                            }
                        }
                        this.Label.className = ""
                        this.CheckBox.checked = "";
                    }
                }
            }

            node.ExpandAll = function () {
                if (this.Nodes[id].State = "Collapse") {
                    this.Nodes[id].State = "Expand";
                    this.Nodes[id].Mark.ChangeMark();
                }
                this.Nodes[id].ExpandAll();
            }
            node.CollapseAll = function () {
                if (this.Nodes[id].State = "Expand") {
                    this.Nodes[id].State = "Collapse";
                    this.Nodes[id].Mark.ChangeMark();
                }
                this.Nodes[id].CollapseAll();
            }
            node.Mark.ChangeMark();
            return node;
        };
        tree.ExpandAll = function () {
            for (var id in tree.Nodes) {
                if (tree.Nodes[id].State = "Collapse") {
                    tree.Nodes[id].State = "Expand";
                    tree.Nodes[id].Mark.ChangeMark();
                }
                tree.Nodes[id].ExpandAll();
            }
        }
        tree.CollapseAll = function () {
            if (tree.Nodes[id].State = "Expand") {
                tree.Nodes[id].State = "Collapse";
                tree.Nodes[id].Mark.ChangeMark();
            }
            tree.Nodes[id].CollapseAll();
        }

        tree.InitTree = function (treeinfo, treedefine) {
            if (!treedefine) {
                treedefine = { Indx: "Indx", Caption: "Caption", Icon: "Icon", Url: "URL", Parent: "Parent" };
            }
            for (var i = 0; i < treeinfo.length; i++) {
                var nodedefine = treeinfo[i];
                var nodeitem = tree.AddNodes(nodedefine[treedefine.Indx], nodedefine[treedefine.Caption], nodedefine[treedefine.Parent], nodedefine[treedefine.Icon], nodedefine[treedefine.Url]);
                nodeitem.TreeDefine = treedefine;
                nodeitem.NodeData = nodedefine;
            }
        }

        window[id] = tree;
    }
	//var treeLast="<div class=\"sidebar-collapse\" id=\"sidebar-collapse\"><i class=\"icon-double-angle-left\" data-icon1=\"icon-double-angle-left\" data-icon2=\"icon-double-angle-right\"></i></div>";
    return tree;
}