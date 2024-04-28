package com.easy.thread;

import com.easy.exception.LegendException;
import com.easy.session.Operator;
import com.easy.utility.SysUtility;

/**
 * so-easy private 异步任务 基础类
 * 
 * @author yewh 2015-09-26
 * 
 * @version 7.0.0
 * 
 */
public abstract class JobDetail {

	private Operator operator;
	
	private boolean finished = false;
	
	protected JobDetail() {
		operator = SysUtility.getCurrentOperator();
	}

	public abstract void run() throws LegendException;

	public Operator getOperator() {
		return operator;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
