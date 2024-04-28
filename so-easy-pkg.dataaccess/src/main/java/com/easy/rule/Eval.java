package com.easy.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.easy.exception.ERR;
import com.easy.exception.LegendException;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class Eval {
	public int eval(String exp) throws LegendException {
		exp = exp + "#";
		int result = 0;
		List list = infixExpToPostExp(exp);// 转化成后缀表达式
		try {
			result = doEval(list);
		} catch (Exception ex) {
			throw LegendException.getLegendException(ERR.INVALID_RULE_EXPRESS);
		}
		return result;// 真正求值
	}

	// 遇到操作符压栈，遇到表达式从后缀表达式中弹出两个数，计算出结果，压入堆栈
	private int doEval(List list) throws LegendException {
		Stack stack = new Stack();
		String element;
		int n1, n2, result;
		try {
			for (int i = 0; i < list.size(); i++) {
				element = (String) list.get(i);
				if (isOperator(element)) {
					n1 = Integer.parseInt((String) stack.pop());
					n2 = Integer.parseInt((String) stack.pop());
					result = doOperate(n1, n2, element);
					stack.push(new Integer(result).toString());
				} else {
					stack.push(element);
				}
			}
			return Integer.parseInt((String) stack.pop());
		} catch (Exception e) {
			throw LegendException.getLegendException(ERR.INVALID_RULE_EXPRESS);
		}
	}

	private int doOperate(int n1, int n2, String operator) {
		if (operator.equals("+"))
			return n1 + n2;
		else if (operator.equals("-"))
			return n1 - n2;
		else if (operator.equals("*"))
			return n1 * n2;
		else
			return n1 / n2;
	}

	private boolean isOperator(String str) {
		return str.equals("+") || str.equals("-") || str.equals("*")
				|| str.equals("/");
	}

	private List infixExpToPostExp(String exp) throws LegendException {// 将中缀表达式转化成为后缀表达式
		List postExp = new ArrayList();// 存放转化的后缀表达式的链表
		StringBuffer numBuffer = new StringBuffer();// 用来保存一个数的
		Stack opStack = new Stack();// 操作符栈
		char ch, preChar;

		opStack.push(new Character('#'));
		try {
			for (int i = 0; i < exp.length();) {
				ch = exp.charAt(i);
				switch (ch) {
				case '+':
				case '-':
				case '*':
				case '/':
					preChar = ((Character) opStack.peek()).charValue();
					// 如果栈里面的操作符优先级比当前的大，则把栈中优先级大的都添加到后缀表达式列表中
					while (priority(preChar) >= priority(ch)) {
						postExp.add("" + preChar);
						opStack.pop();
						preChar = ((Character) opStack.peek()).charValue();
					}
					opStack.push(new Character(ch));
					i++;
					break;
				case '(':
					// 左括号直接压栈
					opStack.push(new Character(ch));
					i++;
					break;
				case ')':
					// 右括号则直接把栈中左括号前面的弹出，并加入后缀表达式链表中
					char c = ((Character) opStack.pop()).charValue();
					while (c != '(') {
						postExp.add("" + c);
						c = ((Character) opStack.pop()).charValue();
					}
					i++;
					break;
				// #号，代表表达式结束，可以直接把操作符栈中剩余的操作符全部弹出，并加入后缀表达式链表中
				case '#':
					char c1;
					while (!opStack.isEmpty()) {
						c1 = ((Character) opStack.pop()).charValue();
						if (c1 != '#')
							postExp.add("" + c1);
					}
					i++;
					break;
				// 过滤空白符
				case ' ':
				case '\t':
				case '\r':
				case '\n':
					i++;
					break;
				// 数字则凑成一个整数，加入后缀表达式链表中
				default:
					if (Character.isDigit(ch)) {
						while (Character.isDigit(ch)) {
							numBuffer.append(ch);
							ch = exp.charAt(++i);
						}
						postExp.add(numBuffer.toString());
						numBuffer = new StringBuffer();
					} else {
						throw LegendException.getLegendException(ERR.INVALID_RULE_EXPRESS);
					}
				}
			}
		} catch (Exception e) {
			throw LegendException.getLegendException(ERR.INVALID_RULE_EXPRESS);
		}
		return postExp;
	}

	private int priority(char op) {
		// 定义优先级
		switch (op) {
		case '+':
		case '-':
			return 1;
		case '*':
		case '/':
			return 2;
		case '(':
			return 0;
		case '#':
			return 0;
		}
		return 0;
	}
}