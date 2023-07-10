package com.lzy.learning.leecode.algrorism;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class StackAndQueue {
}


class Solution247 {
    public static void main(String[] args) {
        final int[] rst = new Solution247().topKFrequent(new int[]{1, 2}, 2);
        System.out.printf("the result is %s\n", Arrays.stream(rst).boxed().collect(Collectors.toList()));
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> maps = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            final int count = maps.getOrDefault(nums[i], 0) + 1;
            maps.put(nums[i], count);
        }

        MaxHeap heap = new MaxHeap(nums.length);
        for (int key : maps.keySet()) {
            heap.push(key, maps.get(key));
        }

        int[] rst = new int[k];
        for (int i = 0; i < k; i++) {
            rst[i] = heap.pop();
        }

        return rst;
    }

    static class NumRepeatCounter implements Comparable<NumRepeatCounter> {
        int num;
        int count;

        public NumRepeatCounter(int num, int count) {
            this.num = num;
            this.count = count;
        }

        @Override
        public int compareTo(NumRepeatCounter o) {
            if (Objects.isNull(o)) {
                throw new NullPointerException();
            }
            return this.count - o.count;
        }
    }

    static class MaxHeap {
        NumRepeatCounter[] heap;
        int size;

        public MaxHeap(int maxSize) {
            heap = new NumRepeatCounter[maxSize];
        }

        void push(int num, int count) {
            int insertIdx = size;
            heap[size++] = new NumRepeatCounter(num, count);

            while (insertIdx - 2 > 0 && heap[insertIdx].compareTo(heap[insertIdx - 2]) > 0) {
                NumRepeatCounter temp = heap[insertIdx - 2];
                heap[insertIdx - 2] = heap[insertIdx];
                heap[insertIdx] = temp;
                insertIdx -= 2;
            }

            if (insertIdx - 2 <= 0 && heap[insertIdx].compareTo(heap[0]) > 0) {
                NumRepeatCounter temp = heap[0];
                heap[0] = heap[insertIdx];
                heap[insertIdx] = temp;
            }
        }

        int pop() {
            NumRepeatCounter popValue = heap[0];

            if (size > 1) {
                int start = (size == 2 || heap[1].compareTo(heap[2]) > 0) ? 1 : 2;
                heap[0] = heap[start];
                while (start + 2 < size) {
                    heap[start] = heap[start + 2];
                    start += 2;
                }

                if (start != size - 1) {
                    NumRepeatCounter rePushVal = heap[size - 1];
                    size -= 2;
                    push(rePushVal.num, rePushVal.count);
                } else {
                    size--;
                }
            }

            return popValue.num;
        }
    }
}

class Solution239 {
    public static void main(String[] args) {
        final int[] maxSlidingWindow = new Solution239().maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3);
        System.out.printf("maxSlidingWindow is %s\n", Arrays.stream(maxSlidingWindow).boxed().collect(Collectors.toList()));
    }

    public int[] maxSlidingWindow(int[] nums, int k) {
        TopQueue topQueue = new TopQueue();
        int[] result = new int[nums.length - k + 1];
        for (int slow = 0, fast = 0; fast < nums.length; fast++) {
            topQueue.push(nums[fast]);
            if (fast - slow + 1 == k) {
                result[slow] = topQueue.top();
                topQueue.pop(nums[slow++]);
            }
        }
        return result;
    }

    static class TopQueue {
        private Deque<Integer> deque = new LinkedList<>();

        public void push(Integer value) {
            while (!deque.isEmpty() && deque.peekFirst() < value) {
                deque.pollFirst();
            }
            deque.addFirst(value);
        }

        public void pop(Integer value) {
            if (deque.peekLast().equals(value)) {
                deque.pollLast();
            }
        }

        public Integer top() {
            return deque.peekLast();
        }
    }
}

/**
 * 给你一个字符串数组 tokens ，表示一个根据 逆波兰表示法 表示的算术表达式。
 * <p>
 * 请你计算该表达式。返回一个表示表达式值的整数。
 * <p>
 * 注意：
 * <p>
 * 有效的算符为 '+'、'-'、'*' 和 '/' 。
 * 每个操作数（运算对象）都可以是一个整数或者另一个表达式。
 * 两个整数之间的除法总是 向零截断 。
 * 表达式中不含除零运算。
 * 输入是一个根据逆波兰表示法表示的算术表达式。
 * 答案及所有中间计算结果可以用 32 位 整数表示。
 * 示例 1：
 * <p>
 * 输入：tokens = ["2","1","+","3","*"]
 * 输出：9
 * 解释：该算式转化为常见的中缀算术表达式为：((2 + 1) * 3) = 9
 * 示例 2：
 * <p>
 * 输入：tokens = ["4","13","5","/","+"]
 * 输出：6
 * 解释：该算式转化为常见的中缀算术表达式为：(4 + (13 / 5)) = 6
 * 示例 3：
 * <p>
 * 输入：tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
 * 输出：22
 * 解释：该算式转化为常见的中缀算术表达式为：
 * ((10 * (6 / ((9 + 3) * -11))) + 17) + 5
 * = ((10 * (6 / (12 * -11))) + 17) + 5
 * = ((10 * (6 / -132)) + 17) + 5
 * = ((10 * 0) + 17) + 5
 * = (0 + 17) + 5
 * = 17 + 5
 * = 22
 * 提示：
 * <p>
 * 1 <= tokens.length <= 104
 * tokens[i] 是一个算符（"+"、"-"、"*" 或 "/"），或是在范围 [-200, 200] 内的一个整数
 * 逆波兰表达式：
 * <p>
 * 逆波兰表达式是一种后缀表达式，所谓后缀就是指算符写在后面。
 * <p>
 * 平常使用的算式则是一种中缀表达式，如 ( 1 + 2 ) * ( 3 + 4 ) 。
 * 该算式的逆波兰表达式写法为 ( ( 1 2 + ) ( 3 4 + ) * ) 。
 * 逆波兰表达式主要有以下两个优点：
 * <p>
 * 去掉括号后表达式无歧义，上式即便写成 1 2 + 3 4 + * 也可以依据次序计算出正确结果。
 * 适合用栈操作运算：遇到数字则入栈；遇到算符则取出栈顶两个数字进行计算，并将结果压入栈中
 */
class Solution150 {
    public static void main(String[] args) {
        System.out.printf("result is %s\n", new Solution150().evalRPN2(new String[]{"4", "13", "5", "/", "+"}));
    }

    public int evalRPN2(String[] tokens) {
        if (tokens.length == 1) {
            return Integer.parseInt(tokens[0]);
        }

        int slow = -1, fast = 0;
        String invalid = "";
        while (fast < tokens.length) {
            String s = tokens[fast];
            BiFunction<Integer, Integer, Integer> consumer;
            switch (s) {
                case "+":
                    consumer = Integer::sum;
                    break;
                case "-":
                    consumer = (n1, n2) -> n1 - n2;
                    break;
                case "*":
                    consumer = (n1, n2) -> n1 * n2;
                    break;
                case "/":
                    consumer = (n1, n2) -> n1 / n2;
                    break;
                default:
                    consumer = null;
            }

            if (consumer != null) {
                tokens[fast] = invalid;
                int right = Integer.parseInt(tokens[slow]);
                tokens[slow] = invalid;
                do {
                    slow--;
                } while (slow >= 0 && tokens[slow].isEmpty());
                int left = Integer.parseInt(tokens[slow]);
                tokens[slow] = consumer.apply(left, right).toString();
            } else {
                do {
                    slow++;
                } while (tokens[slow].isEmpty());
                ;
            }
            fast++;
        }
        return Integer.parseInt(tokens[slow]);
    }

    public int evalRPN(String[] tokens) {
        Stack<String> stack = new Stack<>();
        for (String s : tokens) {
            Optional<BiFunction<Integer, Integer, Integer>> consumer;
            switch (s) {
                case "+":
                    consumer = Optional.of(Integer::sum);
                    break;
                case "-":
                    consumer = Optional.of((n1, n2) -> n1 - n2);
                    break;
                case "*":
                    consumer = Optional.of((n1, n2) -> n1 * n2);
                    break;
                case "/":
                    consumer = Optional.of((n1, n2) -> n1 / n2);
                    break;
                default:
                    consumer = Optional.empty();
            }

            if (consumer.isPresent()) {
                int right = Integer.parseInt(stack.pop());
                int left = Integer.parseInt(stack.pop());
                stack.push(consumer.get().apply(left, right).toString());
            } else {
                stack.push(s);
            }
        }
        return Integer.parseInt(stack.pop());
    }
}

class Solution1047Ver2 {
    public static void main(String[] args) {
        System.out.printf("result is %s\n", new Solution1047Ver2().removeDuplicates("abbaca"));
    }

    public String removeDuplicates(String s) {
        if (Objects.isNull(s) || s.length() <= 1) {
            return s;
        }
        final char[] chars = s.toCharArray();
        int slow = -1;
        int fast = 0;
        while (fast < s.length()) {
            if (slow >= 0 && chars[slow] == chars[fast]) {
                chars[fast++] = 'a' - 1;
                chars[slow--] = 'a' - 1;
            } else {
                slow++;
                if (slow != fast) {
                    chars[slow] = chars[fast];
                    chars[fast] = 'a' - 1;
                }
                fast++;
            }
        }
        return new String(chars).substring(0, slow + 1);
    }
}


/**
 * 给出由小写字母组成的字符串 S，重复项删除操作会选择两个相邻且相同的字母，并删除它们。
 * <p>
 * 在 S 上反复执行重复项删除操作，直到无法继续删除。
 * <p>
 * 在完成所有重复项删除操作后返回最终的字符串。答案保证唯一。
 * <p>
 * 示例：
 * <p>
 * 输入："abbaca"
 * 输出："ca"
 * 解释：
 * 例如，在 "abbaca" 中，我们可以删除 "bb" 由于两字母相邻且相同，这是此时唯一可以执行删除操作的重复项。之后我们得到字符串 "aaca"，其中又只有 "aa" 可以执行重复项删除操作，所以最后的字符串为 "ca"。
 * 提示：
 * <p>
 * 1 <= S.length <= 20000
 * S 仅由小写英文字母组成。
 */
class Solution1047 {
    public String removeDuplicates(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && c == stack.peek()) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        StringBuffer sb = new StringBuffer();
        while (!stack.isEmpty()) {
            sb.insert(0, stack.pop());
        }
        return sb.toString();
    }
}

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。
 * <p>
 * 有效字符串需满足：
 * <p>
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 每个右括号都有一个对应的相同类型的左括号。
 * 示例 1：
 * <p>
 * 输入：s = "()"
 * 输出：true
 * 示例 2：
 * <p>
 * 输入：s = "()[]{}"
 * 输出：true
 * 示例 3：
 * <p>
 * 输入：s = "(]"
 * 输出：false
 * 提示：
 * <p>
 * 1 <= s.length <= 104
 * s 仅由括号 '()[]{}' 组成
 */
class Solution20 {

    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if ('(' == c || '[' == c || '{' == c) {
                stack.push(c);
            } else {
                char pop = stack.pop();
                if (('(' != pop && ')' == c) || ('{' != pop && '}' == c) || ('[' != pop && ']' == c)) {
                    return false;
                }
            }
        }
        return true;
    }
}

class MyStack {
    Queue<Integer> queueEmpty;
    Queue<Integer> queueFull;

    public MyStack() {
        queueEmpty = new LinkedList<>();
        queueFull = new LinkedList<>();
    }

    public void push(int x) {
        if (queueFull.size() == 0) {
            queueFull.add(x);
            return;
        }

        queueEmpty.add(x);
        while (queueFull.size() != 0) {
            queueEmpty.add(queueFull.poll());
        }

        Queue<Integer> t = queueEmpty;
        queueEmpty = queueFull;
        queueFull = t;
    }

    public int pop() {
        return queueFull.poll();
    }

    public int top() {
        return queueFull.peek();
    }

    public boolean empty() {
        return queueFull.size() == 0;
    }
}

/**
 * 请你仅使用两个栈实现先入先出队列。队列应当支持一般队列支持的所有操作（push、pop、peek、empty）：
 * <p>
 * 实现 MyQueue 类：
 * <p>
 * void push(int x) 将元素 x 推到队列的末尾
 * int pop() 从队列的开头移除并返回元素
 * int peek() 返回队列开头的元素
 * boolean empty() 如果队列为空，返回 true ；否则，返回 false
 * 说明：
 * <p>
 * 你 只能 使用标准的栈操作 —— 也就是只有 push to top, peek/pop from top, size, 和 is empty 操作是合法的。
 * 你所使用的语言也许不支持栈。你可以使用 list 或者 deque（双端队列）来模拟一个栈，只要是标准的栈操作即可。
 * 示例 1：
 * <p>
 * 输入：
 * ["MyQueue", "push", "push", "peek", "pop", "empty"]
 * [[], [1], [2], [], [], []]
 * 输出：
 * [null, null, null, 1, 1, false]
 * <p>
 * 解释：
 * MyQueue myQueue = new MyQueue();
 * myQueue.push(1); // queue is: [1]
 * myQueue.push(2); // queue is: [1, 2] (leftmost is front of the queue)
 * myQueue.peek(); // return 1
 * myQueue.pop(); // return 1, queue is [2]
 * myQueue.empty(); // return false
 * 提示：
 * <p>
 * 1 <= x <= 9
 * 最多调用 100 次 push、pop、peek 和 empty
 * 假设所有操作都是有效的 （例如，一个空的队列不会调用 pop 或者 peek 操作）
 * 进阶：
 * <p>
 * 你能否实现每个操作均摊时间复杂度为 O(1) 的队列？换句话说，执行 n 个操作的总时间复杂度为 O(n) ，即使其中一个操作可能花费较长时间。
 */
class MyQueue {
    Stack<Integer> stackEmpty = new Stack<>();
    Stack<Integer> stackFull = new Stack<>();

    public MyQueue() {
    }

    public void push(int x) {
        if (stackFull.isEmpty()) {
            stackFull.push(x);
            return;
        }
        while (!stackFull.isEmpty()) {
            stackEmpty.push(stackFull.pop());
        }
        stackEmpty.push(x);
        while (!stackEmpty.isEmpty()) {
            stackFull.push(stackEmpty.pop());
        }
    }

    public int pop() {
        return stackFull.pop();
    }

    public int peek() {
        return stackFull.peek();
    }

    public boolean empty() {
        return stackFull.isEmpty();
    }
}


