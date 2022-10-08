package pers.kun.permission.service;

import pers.kun.permission.model.MenuTreeNodeVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : qihang.liu
 * @date 2022-04-21
 */
public class MenuTreeBuilder {

    public List<MenuTreeNodeVO> buildByRecursive(List<MenuTreeNodeVO> treeNodes) {
        List<MenuTreeNodeVO> trees = new ArrayList<>();
        for (MenuTreeNodeVO treeNode : treeNodes) {
            if (null == treeNode.getParentId() || 0 == treeNode.getParentId()) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    private MenuTreeNodeVO findChildren(MenuTreeNodeVO treeNode, List<MenuTreeNodeVO> treeNodes) {
        for (MenuTreeNodeVO it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

}
