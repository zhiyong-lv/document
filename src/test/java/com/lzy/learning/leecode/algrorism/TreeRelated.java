package com.lzy.learning.leecode.algrorism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeRelated {
}

class Solution538 {
    public TreeNode convertBST(TreeNode root) {
        traversal(root, 0);
        return root;
    }

    int traversal(TreeNode node, int sum) {
        if (Objects.isNull(node)) {
            return sum;
        }

        int sumAllFromRight = traversal(node.right, sum);
        node.val += sumAllFromRight;
        return traversal(node.left, node.val);
    }
}

class Solution108 {
    public TreeNode sortedArrayToBST(int[] nums) {
        return sortedArrayToBST(nums, 0, nums.length);
    }

    TreeNode sortedArrayToBST(int[] nums, int start, int end) {
        if (end <= start) return null;
        int midIdx = (start + end) / 2;
        TreeNode node = new TreeNode(nums[midIdx]);
        node.left = sortedArrayToBST(nums, start, midIdx);
        node.right = sortedArrayToBST(nums, midIdx + 1, end);
        return node;
    }
}

class Solution669 {
    public TreeNode trimBST(TreeNode root, int low, int high) {
        TreeNode node = root;
        while (Objects.nonNull(node)) {
            if (node.val > high) node = node.left;
            else if (node.val < low) node = node.right;
            else break;
        }

        if (Objects.nonNull(node)) {
            node.left = trimBST(node.left, low, high);
            node.right = trimBST(node.right, low, high);
        }
        return node;
    }
}

class Solution450 {
    public TreeNode deleteNode(TreeNode root, int key) {
        if (Objects.isNull(root)) return null;
        if (key == root.val) {
            return replaceNode(root);
        } else if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else {
            root.right = deleteNode(root.right, key);
        }
        return root;
    }

    TreeNode replaceNode(TreeNode find) {
        if (Objects.isNull(find.left) && Objects.isNull(find.right)) {
            return null;
        }
        if (Objects.isNull(find.left)) return find.right;
        if (Objects.isNull(find.right)) return find.left;
        TreeNode insertNode = find.right;
        while (Objects.nonNull(insertNode.left)) {
            insertNode = insertNode.left;
        }
        insertNode.left = find.left;
        return find.right;
    }
}

class Solution701Iteration {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (Objects.isNull(root)) return new TreeNode(val);
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            final TreeNode node = stack.pop();
            if (val < node.val) {
                if (Objects.isNull(node.left)) {
                    node.left = new TreeNode(val);
                    break;
                } else {
                    stack.push(node.left);
                }
            } else {
                if (Objects.isNull(node.right)) {
                    node.right = new TreeNode(val);
                    break;
                } else {
                    stack.push(node.right);
                }
            }
        }
        return root;
    }
}

class Solution701 {
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (Objects.isNull(root)) return new TreeNode(val);
        traversal(root, val);
        return root;
    }

    void traversal(TreeNode node, int val) {
        if (node.val > val) {
            if (Objects.isNull(node.left)) {
                node.left = new TreeNode(val);
            } else {
                traversal(node.left, val);
            }
        } else {
            if (Objects.isNull(node.right)) {
                node.right = new TreeNode(val);
            } else {
                traversal(node.right, val);
            }
        }
    }
}

class Solution235 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        int max = Math.max(p.val, q.val);
        int min = Math.min(p.val, q.val);
        return lowestCommonAncestor(root, min, max, null, null);
    }

    public TreeNode lowestCommonAncestor(TreeNode node, int min, int max, TreeNode left, TreeNode right) {
        if (Objects.nonNull(left) && (max < left.val || min < left.val)) {
            return null;
        }

        if (Objects.nonNull(right) && (min > right.val || max > right.val)) {
            return null;
        }

        if (max >= node.val && min <= node.val) {
            return node;
        }
        return max <= node.val ? lowestCommonAncestor(node.left, min, max, left, node) :
                lowestCommonAncestor(node.right, min, max, node, right);
    }
}

class Solution236 {
    TreeNode result = null;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        traversal(root, p, q);
        return result;
    }

    int traversal(TreeNode node, TreeNode p, TreeNode q) {
        int leftRst = 0, rightRst = 0;
        if (Objects.nonNull(node.left)) leftRst = traversal(node.left, p, q);
        if (leftRst == 3) {
            return leftRst;
        }
        if (Objects.nonNull(node.right)) rightRst = traversal(node.right, p, q);
        if (rightRst == 3) {
            return rightRst;
        }

        int rst = leftRst | rightRst;
        if (node == p) {
            rst |= 1;
        } else if (node == q) {
            rst |= 2;
        }
        if (rst == 3) {
            result = node;
            return rst;
        } else {
            return rst;
        }
    }

}

class Solution501 {
    Integer lastValue = null;
    int lastValueCnt = 0;
    int maxValueCnt = 0;
    List<Integer> max = new ArrayList<>();

    public int[] findMode(TreeNode root) {
        traversal(root);
        updateMax();
        int[] result = new int[max.size()];
        for (int i = 0; i < max.size(); i++) {
            result[i] = max.get(i);
        }
        return result;
    }

    private void updateMax() {
        if (max.isEmpty() || maxValueCnt < lastValueCnt) {
            max.clear();
            max.add(lastValue);
            maxValueCnt = lastValueCnt;
        } else if (maxValueCnt == lastValueCnt) {
            max.add(lastValue);
        }
    }

    void traversal(TreeNode node) {
        if (Objects.nonNull(node.left)) traversal(node.left);
        if (Objects.isNull(lastValue)) {
            lastValue = node.val;
            lastValueCnt = 1;
        } else if (node.val == lastValue) {
            lastValueCnt++;
        } else {
            updateMax();
            lastValue = node.val;
            lastValueCnt = 1;
        }
        if (Objects.nonNull(node.right)) traversal(node.right);
    }
}

class Solution530 {
    public int getMinimumDifference(TreeNode root) {
        int min = Integer.MAX_VALUE;
        Stack<TreeNode> stack = new Stack<>();
        TreeNode lastNode = null;
        while (Objects.nonNull(root) || !stack.isEmpty()) {
            if (Objects.nonNull(root)) {
                stack.push(root);
                root = root.left;
            } else {
                final TreeNode node = stack.pop();
                if (Objects.nonNull(lastNode)) min = Math.min(min, Math.abs(lastNode.val - node.val));
                lastNode = node;
                if (Objects.nonNull(node.right)) root = node.right;
            }
        }
        return min;
    }


    public int getMinimumDifference2(TreeNode root) {
        int min = Integer.MAX_VALUE;
        Stack<TreeNode> stack = new Stack<>();
        TreeNode lastNode = null;
        while (Objects.nonNull(root) || !stack.isEmpty()) {
            if (Objects.nonNull(root)) {
                stack.push(root);
                root = root.left;
            } else {
                final TreeNode node = stack.pop();
                if (Objects.nonNull(lastNode)) min = Math.min(min, Math.abs(lastNode.val - node.val));
                lastNode = node;
                if (Objects.nonNull(node.right)) root = node.right;
            }
        }
        return min;
    }
}

class Solution98 {
    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, null, null);
    }

    boolean isValidBST(TreeNode node, TreeNode left, TreeNode right) {
        if (Objects.isNull(node)) return true;

        if (Objects.nonNull(left) && left.val >= node.val) return false;
        if (Objects.nonNull(right) && right.val <= node.val) return false;

        return isValidBST(node.left, left, node) && isValidBST(node.right, node, right);
    }
}

class Solution700NewSolution {
    public TreeNode searchBST(TreeNode root, int val) {
        if (Objects.isNull(root)) return null;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            final TreeNode node = stack.pop();
            if (node.val == val) return node;
            else if (val < node.val && Objects.nonNull(node.left)) stack.push(node.left);
            else if (val > node.val && Objects.nonNull(node.right)) stack.push(node.right);
        }
        return null;
    }
}

class Solution700 {
    public TreeNode searchBST(TreeNode root, int val) {
        if (Objects.isNull(root)) return null;

        if (val == root.val) return root;
        else if (val < root.val) return searchBST(root.left, val);
        else return searchBST(root.right, val);
    }
}

class Solution617NewSolution {
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (Objects.isNull(root1) && Objects.isNull(root2)) return null;

        int val1 = 0, val2 = 0;
        TreeNode root1Left = null, root1Right = null, root2Left = null, root2Right = null;
        if (Objects.nonNull(root1)) {
            val1 = root1.val;
            root1Left = root1.left;
            root1Right = root1.right;
        }
        if (Objects.nonNull(root2)) {
            val2 = root2.val;
            root2Left = root2.left;
            root2Right = root2.right;
        }


        TreeNode node = new TreeNode(val1 + val2);
        node.left = mergeTrees(root1Left, root2Left);
        node.right = mergeTrees(root1Right, root2Right);
        return node;
    }
}

class Solution617 {
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (Objects.isNull(root1) && Objects.isNull(root2)) return null;
        TreeNode node = new TreeNode(Stream.of(root1, root2).filter(Objects::nonNull).mapToInt(n -> n.val).sum());
        node.left = mergeTrees(Objects.nonNull(root1) ? root1.left : null, Objects.nonNull(root2) ? root2.left : null);
        node.right = mergeTrees(Objects.nonNull(root1) ? root1.right : null, Objects.nonNull(root2) ? root2.right : null);
        return node;
    }
}

class Solution654 {
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return constructMaximumBinaryTree(nums, 0, nums.length);
    }

    private TreeNode constructMaximumBinaryTree(int[] nums, int start, int end) {
        final int maxIdx = findMaxIdx(nums, start, end);
        TreeNode root = new TreeNode(nums[maxIdx]);
        root.left = maxIdx - start <= 0 ? null : constructMaximumBinaryTree(nums, start, maxIdx);
        root.right = end - maxIdx - 1 <= 0 ? null : constructMaximumBinaryTree(nums, maxIdx + 1, end);
        return root;
    }

    private int findMaxIdx(int[] nums, int start, int end) {
        int max = nums[start];
        int maxIdx = start;
        for (int i = start + 1; i < end; i++) {
            if (max < nums[i]) {
                max = nums[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}

class Solution106 {
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        return buildTree(inorder, 0, inorder.length, postorder, 0, postorder.length);
    }

    TreeNode buildTree(int[] inorder, int inorderStart, int inorderEnd, int[] postorder, int postorderStart, int postorderEnd) {
        final int rootVal = postorder[postorderEnd - 1];
        TreeNode root = new TreeNode(rootVal);
        int rootIdx = -1;
        for (int i = inorderStart; i < inorderEnd; i++) {
            if (rootVal == inorder[i]) {
                rootIdx = i;
                break;
            }
        }
        assert rootIdx >= 0;
        int leftCount = rootIdx - inorderStart;
        int rightCount = inorderEnd - rootIdx - 1;
        root.left = leftCount <= 0 ? null : buildTree(inorder, inorderStart, rootIdx, postorder, postorderStart, postorderStart + (rootIdx - inorderStart));
        root.right = rightCount <= 0 ? null : buildTree(inorder, rootIdx + 1, inorderEnd, postorder, postorderStart + (rootIdx - inorderStart), postorderEnd - 1);
        return root;
    }
}


class Solution113NewSolution {

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;

        Stack<List<Object>> stack = new Stack<>();
        stack.push(Arrays.asList(root, 0, new ArrayList<>()));

        while (!stack.isEmpty()) {
            final List<Object> pop = stack.pop();
            final TreeNode node = (TreeNode) pop.get(0);
            Integer sum = (Integer) pop.get(1);
            List<Integer> list = (List<Integer>) pop.get(2);

            sum += node.val;
            list.add(node.val);
            if (Objects.isNull(node.left) && Objects.isNull(node.right) && sum == targetSum) {
                result.add(list);
                continue;
            }
            if (Objects.nonNull(node.right)) stack.push(Arrays.asList(node.right, sum, new ArrayList<>(list)));
            if (Objects.nonNull(node.left)) stack.push(Arrays.asList(node.left, sum, new ArrayList<>(list)));
        }
        return result;
    }
}

class Solution113 {

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;

        List<Integer> list = new ArrayList<>();
        traversal(root, 0, targetSum, list, result);
        return result;
    }

    private void traversal(TreeNode node, int sum, int targetSum, List<Integer> list, List<List<Integer>> result) {
        sum += node.val;
        list.add(node.val);
        if (Objects.isNull(node.left) && Objects.isNull(node.right) && sum == targetSum) {
            result.add(new ArrayList<>(list));
        }
        if (Objects.nonNull(node.left)) traversal(node.left, sum, targetSum, list, result);
        if (Objects.nonNull(node.right)) traversal(node.right, sum, targetSum, list, result);
        list.remove(list.size() - 1);
    }
}

class Solution112 {
    boolean match = false;

    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (Objects.isNull(root)) return match;
        traversal(root, 0, targetSum);
        return match;
    }

    private void traversal(TreeNode node, int sum, int targetSum) {
        if (match) return;
        sum += node.val;
        if (Objects.isNull(node.left) && Objects.isNull(node.right)) match = sum == targetSum;
        if (!match && Objects.nonNull(node.left)) traversal(node.left, sum, targetSum);
        if (!match && Objects.nonNull(node.right)) traversal(node.right, sum, targetSum);
    }
}

class Solution513NewSolution {
    int maxLevel = 0;
    int val = 0;

    public int findBottomLeftValue(TreeNode root) {
        traversal(root, 1);
        return val;
    }

    private void traversal(TreeNode node, int level) {
        if (Objects.nonNull(node.left)) traversal(node.left, level + 1);
        if (Objects.nonNull(node.right)) traversal(node.right, level + 1);
        if (level > maxLevel) {
            maxLevel = level;
            val = node.val;
        }
    }
}

class Solution513 {
    public int findBottomLeftValue(TreeNode root) {
        Deque<TreeNode> deque = new LinkedList<>();
        deque.addFirst(root);
        int result = root.val;
        while (!deque.isEmpty()) {
            int size = deque.size();
            for (int i = 0; i < size; i++) {
                final TreeNode node = deque.pollLast();
                if (i == 0) {
                    result = node.val;
                }
                if (Objects.nonNull(node.left)) deque.addFirst(node.left);
                if (Objects.nonNull(node.right)) deque.addFirst(node.right);
            }
        }
        return result;
    }
}

class Solution404 {
    public int sumOfLeftLeaves(TreeNode root) {
        if (Objects.isNull(root)) return 0;

        AtomicInteger sum = new AtomicInteger(0);
        sumOfLeftLeaves(root, sum, false);
        return sum.get();
    }

    void sumOfLeftLeaves(TreeNode node, AtomicInteger sum, boolean isLeft) {
        if (isLeft && Objects.isNull(node.left) && Objects.isNull(node.right)) {
            sum.getAndAdd(node.val);
        }

        if (Objects.nonNull(node.left)) {
            sumOfLeftLeaves(node.left, sum, true);
        }

        if (Objects.nonNull(node.right)) {
            sumOfLeftLeaves(node.right, sum, false);
        }
    }
}

class Solution257NewSolution {
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> rst = new ArrayList<>();
        List<Integer> list = new ArrayList<>();

        list.add(root.val);
        backTrace(root, rst, list);
        return rst;
    }

    void backTrace(TreeNode node, List<String> rst, List<Integer> list) {
        if (Objects.isNull(node.left) && Objects.isNull(node.right)) {
            rst.add(list.stream().map(String::valueOf).collect(Collectors.joining("->")));
        }

        Stream.of(node.left, node.right)
                .filter(Objects::nonNull)
                .forEach(child -> {
                    list.add(child.val);
                    backTrace(child, rst, list);
                    list.remove(list.size() - 1);
                });
    }
}

class Solution257 {
    public List<String> binaryTreePaths(TreeNode root) {
        if (Objects.isNull(root.right) && Objects.isNull(root.left)) {
            return Collections.singletonList(String.valueOf(root.val));
        }

        return Stream.of(root.left, root.right)
                .filter(Objects::nonNull)
                .map(node -> binaryTreePaths(node).stream().map(s -> root.val + "->" + s).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}

class Solution110NewSolution {
    public boolean isBalanced(TreeNode root) {
        return Objects.isNull(root) || getLayerCount(root) >= 0;
    }

    private int getLayerCount(TreeNode node) {
        if (Objects.isNull(node)) return 0;

        final int left = getLayerCount(node.left);
        final int right = getLayerCount(node.right);
        return (left < 0 || right < 0 || Math.abs(left - right) > 1) ? -1 : 1 + Math.max(left, right);
    }
}

class Solution110 {
    Map<TreeNode, Integer> cache = new HashMap<>();

    public boolean isBalanced(TreeNode root) {
        return Objects.isNull(root) || (isBalanced(root.left) && isBalanced(root.right) && Math.abs(getLayerCount(root.left) - getLayerCount(root.right)) <= 1);
    }

    private int getLayerCount(TreeNode node) {
        if (Objects.isNull(node)) return 0;

        if (cache.containsKey(node)) {
            return cache.get(node);
        }
        int count = 1 + Math.max(getLayerCount(node.left), getLayerCount(node.right));
        cache.put(node, count);
        return count;
    }
}

class Solution222 {
    public int countNodes(TreeNode root) {
        return Objects.isNull(root) ? 0 : 1 + countNodes(root.left) + countNodes(root.right);
    }
}

class Solution100 {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (Objects.isNull(p) || Objects.isNull(q)) {
            return p == q;
        }

        return (p.val != q.val) && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}

class Solution101NewSolution {
    public boolean isSymmetric(TreeNode root) {
        if (Objects.isNull(root)) return true;

        Deque<TreeNode> deque = new LinkedList<>();
        deque.addFirst(root.left);
        deque.addFirst(root.right);

        while (!deque.isEmpty()) {
            final int size = deque.size();
            for (int i = 0; i < size; i += 2) {
                final TreeNode left = deque.pollLast();
                final TreeNode right = deque.pollLast();

                if (Objects.isNull(left) || Objects.isNull(right)) {
                    if (left == right) continue;
                    return false;
                }

                if (left.val != right.val) return false;
                deque.addFirst(left.left);
                deque.addFirst(right.right);
                deque.addFirst(left.right);
                deque.addFirst(right.left);
            }
        }
        return true;
    }
}

class Solution101 {
    public boolean isSymmetric(TreeNode root) {
        if (Objects.isNull(root)) return true;
        return isSymmetric(root.left, root.right);
    }

    boolean isSymmetric(TreeNode left, TreeNode right) {
        if (Objects.isNull(left) || Objects.isNull(right)) return left == right;
        return left.val == right.val && isSymmetric(left.left, right.right) && isSymmetric(left.right, right.left);
    }
}

class Solution226StackSolution {
    public TreeNode invertTree(TreeNode root) {
        Deque<TreeNode> deque = new LinkedList<>();
        deque.addFirst(root);
        while (!deque.isEmpty()) {
            final TreeNode node = deque.pollLast();
            if (Objects.isNull(node)) continue;
            final TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
            if (Objects.nonNull(node.left)) deque.addFirst(node.left);
            if (Objects.nonNull(node.right)) deque.addFirst(node.right);
        }
        return root;
    }
}

class Solution226NewSolution {
    public TreeNode invertTree(TreeNode root) {
        Deque<TreeNode> deque = new LinkedList<>();
        deque.addFirst(root);
        while (!deque.isEmpty()) {
            final int size = deque.size();
            for (int i = 0; i < size; i++) {
                final TreeNode node = deque.pollLast();
                if (Objects.isNull(node)) continue;
                final TreeNode temp = node.left;
                node.left = node.right;
                node.right = temp;
                if (Objects.nonNull(node.left)) deque.addFirst(node.left);
                if (Objects.nonNull(node.right)) deque.addFirst(node.right);
            }
        }
        return root;
    }
}

class Solution226 {
    public TreeNode invertTree(TreeNode root) {
        invert(root);
        return root;
    }

    void invert(TreeNode node) {
        if (Objects.isNull(node)) {
            return;
        }

        final TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;

        invert(node.left);
        invert(node.right);
    }
}

class Solution111 {
    public int minDepth(TreeNode root) {
        AtomicInteger result = new AtomicInteger(0);
        if (Objects.nonNull(root)) {
            minDepth(Collections.singletonList(root), result);
        }
        return result.get();
    }

    void minDepth(List<TreeNode> layer, AtomicInteger result) {
        if (layer.isEmpty()) return;

        List<TreeNode> newLayer = new ArrayList<>();
        for (TreeNode node : layer) {
            final boolean leftExist = Objects.nonNull(node.left);
            final boolean rightExist = Objects.nonNull(node.right);
            if (!leftExist && !rightExist) {
                result.getAndIncrement();
                return;
            }
            if (leftExist) newLayer.add(node.left);
            if (rightExist) newLayer.add(node.right);
        }
        result.getAndIncrement();
        minDepth(newLayer, result);
    }
}

class Solution104 {

    public int maxDepth(TreeNode root) {
        AtomicInteger result = new AtomicInteger(0);
        if (Objects.nonNull(root)) {
            maxDepth(Collections.singletonList(root), result);
        }
        return result.get();
    }

    void maxDepth(List<TreeNode> layer, AtomicInteger result) {
        if (layer.isEmpty()) return;

        List<TreeNode> newLayer = new ArrayList<>();
        for (TreeNode node : layer) {
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        result.getAndIncrement();
        maxDepth(newLayer, result);
    }
}

class Solution117 {
    public Node connect(Node root) {
        if (Objects.nonNull(root)) {
            connect(Collections.singletonList(root));
        }
        return root;
    }


    void connect(List<Node> layer) {
        if (layer.isEmpty()) return;
        Node last = new Node();
        List<Node> newLayer = new ArrayList<>();
        for (Node node : layer) {
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
            last.next = node;
            last = node;
        }
        last.next = null;
        connect(newLayer);
    }

    static class Node {
        Node left, right, next;
    }
}

class Solution116 {
    public Node connect(Node root) {
        if (Objects.nonNull(root)) {
            connect(Collections.singletonList(root));
        }
        return root;
    }


    void connect(List<Node> layer) {
        if (layer.isEmpty()) return;
        Node last = new Node();
        List<Node> newLayer = new ArrayList<>();
        for (Node node : layer) {
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
            last.next = node;
            last = node;
        }
        connect(newLayer);
    }

    static class Node {
        Node left, right, next;
    }
}

class Solution515 {

    public List<Integer> largestValues(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (Objects.nonNull(root)) {
            largestValues(Collections.singletonList(root), result);
        }
        return result;
    }

    void largestValues(List<TreeNode> layer, List<Integer> result) {
        if (layer.isEmpty()) return;

        List<TreeNode> newLayer = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        for (TreeNode node : layer) {
            max = Math.max(max, node.val);
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        result.add(max);
        largestValues(newLayer, result);
    }
}

class Solution429 {
    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.nonNull(root)) {
            levelOrder(Collections.singletonList(root), result);
        }
        return result;
    }

    void levelOrder(List<Node> layer, List<List<Integer>> result) {
        if (layer.isEmpty()) return;

        List<Integer> layerValues = new ArrayList<>();
        List<Node> nextLayer = new ArrayList<>();
        for (Node node : layer) {
            layerValues.add(node.val);
            if (Objects.nonNull(node.children)) nextLayer.addAll(node.children);
        }
        result.add(layerValues);
        levelOrder(nextLayer, result);
    }
}

class Node {
    Integer val;
    List<Node> children;
}

class Solution637 {
    public List<Double> averageOfLevels(TreeNode root) {
        ArrayList<Double> result = new ArrayList<>();
        if (Objects.nonNull(root)) rightSideView(Collections.singletonList(root), result);
        return result;

    }

    void rightSideView(List<TreeNode> layer, List<Double> result) {
        if (layer.isEmpty()) return;

        List<TreeNode> newLayer = new ArrayList<>();
        double sum = 0;
        for (TreeNode node : layer) {
            sum += node.val;
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        result.add(sum / layer.size());
        rightSideView(newLayer, result);
    }
}

class Solution199 {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (Objects.nonNull(root)) rightSideView(Collections.singletonList(root), result);
        return result;
    }

    void rightSideView(List<TreeNode> layer, List<Integer> result) {
        if (layer.isEmpty()) return;

        List<TreeNode> newLayer = new ArrayList<>();
        for (TreeNode node : layer) {
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        result.add(layer.get(layer.size() - 1).val);
        rightSideView(newLayer, result);
    }
}

class Solution107 {
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;
        levelOrderBottom(Arrays.asList(root), result);
        return result;
    }

    private void levelOrderBottom(List<TreeNode> layer, List<List<Integer>> result) {
        if (layer.size() == 0) {
            return;
        }

        List<TreeNode> newLayer = new ArrayList<>();
        List<Integer> layerValues = new ArrayList<>();
        for (TreeNode node : layer) {
            layerValues.add(node.val);
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        levelOrderBottom(newLayer, result);
        result.add(layerValues);
    }
}

class Solution102NewSolutionRecursive {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;
        levelOrder(Arrays.asList(root), result);
        return result;
    }

    private void levelOrder(List<TreeNode> layer, List<List<Integer>> result) {
        if (layer.size() == 0) {
            return;
        }

        List<TreeNode> newLayer = new ArrayList<>();
        List<Integer> layerValues = new ArrayList<>();
        for (TreeNode node : layer) {
            layerValues.add(node.val);
            if (Objects.nonNull(node.left)) newLayer.add(node.left);
            if (Objects.nonNull(node.right)) newLayer.add(node.right);
        }
        result.add(layerValues);
        levelOrder(newLayer, result);
    }
}

class Solution102NewSolutionList {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;

        List<Integer> layerValues = new ArrayList<>();
        List<TreeNode> layerNodes = new ArrayList<>();
        List<TreeNode> nextLayerNodes = new ArrayList<>();
        layerNodes.add(root);

        while (!layerNodes.isEmpty()) {
            for (TreeNode node : layerNodes) {
                layerValues.add(node.val);
                if (Objects.nonNull(node.left)) nextLayerNodes.add(node.left);
                if (Objects.nonNull(node.right)) nextLayerNodes.add(node.right);
            }
            result.add(layerValues);
            layerValues = new ArrayList<>();
            layerNodes = nextLayerNodes;
            nextLayerNodes = new ArrayList<>();
        }
        return result;
    }
}

class Solution102NewSolutionStack {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (Objects.isNull(root)) return result;

        Deque<TreeNode> stack = new LinkedList<>();
        List<Integer> layerValues = new ArrayList<>();
        stack.addFirst(root);
        stack.addFirst(null);
        while (!stack.isEmpty()) {
            final TreeNode node = stack.removeLast();
            if (Objects.nonNull(node)) {
                if (Objects.nonNull(node.left)) stack.addFirst(node.left);
                if (Objects.nonNull(node.right)) stack.addFirst(node.right);
                layerValues.add(node.val);
            } else {
                if (!stack.isEmpty()) stack.addFirst(null);
                result.add(layerValues);
                layerValues = new ArrayList<>();
            }
        }
        return result;
    }
}

class Solution102 {
    public List<List<Integer>> levelOrder(TreeNode root) {
        if (Objects.isNull(root)) {
            return new ArrayList<>();
        }

        List<TreeNode> layerNodes = new ArrayList<>();
        List<TreeNode> nextLayerNodes = new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();

        layerNodes.add(root);
        while (layerNodes.size() > 0) {
            List<Integer> layerValues = new ArrayList<>(layerNodes.size());

            for (final TreeNode node : layerNodes) {
                if (Objects.nonNull(node.left)) nextLayerNodes.add(node.left);
                if (Objects.nonNull(node.right)) nextLayerNodes.add(node.right);
                layerValues.add(node.val);
            }

            result.add(layerValues);
            layerNodes = nextLayerNodes;
            nextLayerNodes = new ArrayList<>();
        }
        return result;
    }
}

class Solution144NewSolution2 {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        if (Objects.isNull(root)) {
            return rst;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            TreeNode node = stack.pop();
            if (Objects.nonNull(node)) {
                if (Objects.nonNull(node.right)) stack.push(node.right);
                if (Objects.nonNull(node.left)) stack.push(node.left);
                stack.push(node);
                stack.push(null);
            } else {
                node = stack.pop();
                rst.add(node.val);
            }
        }
        return rst;
    }
}

class Solution144NewSolution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        if (Objects.isNull(root)) {
            return rst;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            TreeNode node = stack.pop();
            rst.add(node.val);
            if (Objects.nonNull(node.right)) stack.push(node.right);
            if (Objects.nonNull(node.left)) stack.push(node.left);
        }
        return rst;
    }
}

class Solution94NewSolution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        if (Objects.isNull(root)) {
            return rst;
        }
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = root;
        while (Objects.nonNull(node) || !stack.isEmpty()) {
            if (Objects.nonNull(node)) {
                stack.push(node);
                node = node.left;
            } else {
                node = stack.pop();
                rst.add(node.val);
                node = node.right;
            }
        }
        return rst;
    }
}

class Solution94NewSolution2 {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        if (Objects.isNull(root)) {
            return rst;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (Objects.nonNull(node)) {
                if (Objects.nonNull(node.right)) stack.push(node.right);
                stack.push(node);
                stack.push(null);
                if (Objects.nonNull(node.left)) stack.push(node.left);
            } else {
                node = stack.pop();
                rst.add(node.val);
            }
        }
        return rst;
    }
}

class Solution94 {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        traversal(root, rst);
        return rst;
    }

    void traversal(TreeNode root, List<Integer> rst) {
        if (Objects.isNull(root)) {
            return;
        }
        traversal(root.left, rst);
        rst.add(root.val);
        traversal(root.right, rst);
    }
}

class Solution145 {
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        traversal(root, rst);
        return rst;
    }

    void traversal(TreeNode root, List<Integer> rst) {
        if (Objects.isNull(root)) {
            return;
        }
        traversal(root.left, rst);
        traversal(root.right, rst);
        rst.add(root.val);
    }
}

class Solution144 {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> rst = new ArrayList<>();
        traversal(root, rst);
        return rst;
    }

    void traversal(TreeNode root, List<Integer> rst) {
        if (Objects.isNull(root)) {
            return;
        }
        rst.add(root.val);
        traversal(root.left, rst);
        traversal(root.right, rst);
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}


