/**
 * 
 */
package com.ruyicai.charge.dna.v2.common;

/** DNA服务, 实现此接口的程序将会被统一管理
 * @author lakey
 *
 */
public interface Service {
	/**服务名称
	 * @return 服务名称
	 */
	public String getName();
	
	/**该服务的描述
	 * @return 服务描述
	 */
	public String getDescribtion();
	
	/**是否已经启动
	 * @return 是否已经启动
	 */
	public boolean isStarted();
	
	/**初始化数据，服务启动前会被调用
	 */
	public void init();
	
	/**启动服务，设置服务状态为已经启动
	 * 如果服务已经启动，此函数不做任何操作。
	 */
	public void start();
	
	/**停止服务，设置服务状态为未启动
	 * 如果服务未启动，此函数不做任何操作。
	 */
	public void stop();
		
	/**关闭服务，释放所有资源。
	 * 如果服务未停止，此函数先调用 stop() 函数，然后再关闭。
	 */
	public void close();
}
