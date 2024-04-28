package com.easy.app.entity;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.impl.ContentHeaderPropertyWriter;

public class RabbitBasicProperties extends BasicProperties{

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#appendPropertyDebugStringTo(java.lang.StringBuilder)
	 */
	@Override
	public void appendPropertyDebugStringTo(StringBuilder acc) {
		// TODO Auto-generated method stub
		super.appendPropertyDebugStringTo(acc);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#builder()
	 */
	@Override
	public Builder builder() {
		// TODO Auto-generated method stub
		return super.builder();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getAppId()
	 */
	@Override
	public String getAppId() {
		// TODO Auto-generated method stub
		return super.getAppId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getClassId()
	 */
	@Override
	public int getClassId() {
		// TODO Auto-generated method stub
		return super.getClassId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getClassName()
	 */
	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return super.getClassName();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getClusterId()
	 */
	@Override
	public String getClusterId() {
		// TODO Auto-generated method stub
		return super.getClusterId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getContentEncoding()
	 */
	@Override
	public String getContentEncoding() {
		// TODO Auto-generated method stub
		return super.getContentEncoding();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getContentType()
	 */
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return super.getContentType();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getCorrelationId()
	 */
	@Override
	public String getCorrelationId() {
		// TODO Auto-generated method stub
		return super.getCorrelationId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getDeliveryMode()
	 */
	@Override
	public Integer getDeliveryMode() {
		// TODO Auto-generated method stub
		return super.getDeliveryMode();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getExpiration()
	 */
	@Override
	public String getExpiration() {
		// TODO Auto-generated method stub
		return super.getExpiration();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getHeaders()
	 */
	@Override
	public Map<String, Object> getHeaders() {
		// TODO Auto-generated method stub
		return super.getHeaders();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getMessageId()
	 */
	@Override
	public String getMessageId() {
		// TODO Auto-generated method stub
		return super.getMessageId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getPriority()
	 */
	@Override
	public Integer getPriority() {
		// TODO Auto-generated method stub
		return super.getPriority();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getReplyTo()
	 */
	@Override
	public String getReplyTo() {
		// TODO Auto-generated method stub
		return super.getReplyTo();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getTimestamp()
	 */
	@Override
	public Date getTimestamp() {
		// TODO Auto-generated method stub
		return super.getTimestamp();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return super.getType();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#getUserId()
	 */
	@Override
	public String getUserId() {
		// TODO Auto-generated method stub
		return super.getUserId();
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setAppId(java.lang.String)
	 */
	@Override
	public void setAppId(String appId) {
		// TODO Auto-generated method stub
		super.setAppId(appId);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setClusterId(java.lang.String)
	 */
	@Override
	public void setClusterId(String clusterId) {
		// TODO Auto-generated method stub
		super.setClusterId(clusterId);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setContentEncoding(java.lang.String)
	 */
	@Override
	public void setContentEncoding(String contentEncoding) {
		// TODO Auto-generated method stub
		super.setContentEncoding(contentEncoding);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType) {
		// TODO Auto-generated method stub
		super.setContentType(contentType);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setCorrelationId(java.lang.String)
	 */
	@Override
	public void setCorrelationId(String correlationId) {
		// TODO Auto-generated method stub
		super.setCorrelationId(correlationId);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setDeliveryMode(java.lang.Integer)
	 */
	@Override
	public void setDeliveryMode(Integer deliveryMode) {
		// TODO Auto-generated method stub
		super.setDeliveryMode(deliveryMode);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setExpiration(java.lang.String)
	 */
	@Override
	public void setExpiration(String expiration) {
		// TODO Auto-generated method stub
		super.setExpiration(expiration);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setHeaders(java.util.Map)
	 */
	@Override
	public void setHeaders(Map<String, Object> headers) {
		// TODO Auto-generated method stub
		super.setHeaders(headers);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setMessageId(java.lang.String)
	 */
	@Override
	public void setMessageId(String messageId) {
		// TODO Auto-generated method stub
		super.setMessageId(messageId);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setPriority(java.lang.Integer)
	 */
	@Override
	public void setPriority(Integer priority) {
		// TODO Auto-generated method stub
		super.setPriority(priority);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setReplyTo(java.lang.String)
	 */
	@Override
	public void setReplyTo(String replyTo) {
		// TODO Auto-generated method stub
		super.setReplyTo(replyTo);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setTimestamp(java.util.Date)
	 */
	@Override
	public void setTimestamp(Date timestamp) {
		// TODO Auto-generated method stub
		super.setTimestamp(timestamp);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub
		super.setType(type);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#setUserId(java.lang.String)
	 */
	@Override
	public void setUserId(String userId) {
		// TODO Auto-generated method stub
		super.setUserId(userId);
	}

	/* (non-Javadoc)
	 * @see com.rabbitmq.client.AMQP.BasicProperties#writePropertiesTo(com.rabbitmq.client.impl.ContentHeaderPropertyWriter)
	 */
	@Override
	public void writePropertiesTo(ContentHeaderPropertyWriter writer)
			throws IOException {
		// TODO Auto-generated method stub
		super.writePropertiesTo(writer);
	}
	
}
