package com.xd.demo.print.listener;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

/**
 * 节点监听器，任务监听器，连线监听器
 *
 * @author wsylp
 *
 */
@Slf4j
public class MyTaskListener implements TaskListener {

    private static final long serialVersionUID = 6200534335483960408L;

    private Expression arg;

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }

    @Override
    public void notify(DelegateTask delegateTask) {

        // 实现TaskListener中的方法
        String eventName = delegateTask.getEventName();
        //log.info("任务监听器:{}", arg.getValue(delegateTask));
        log.info("时间任务{}",eventName);
        /* if(!"create".endsWith(eventName))
             throw new RuntimeException("123");*/
        if ("create".endsWith(eventName)) {
            log.info("create=========");
        } else if ("assignment".endsWith(eventName)) {
            log.info("assignment========");
        } else if ("complete".endsWith(eventName)) {

            log.info("complete===========");
           // throw new RuntimeException("123");
        } else if ("delete".endsWith(eventName)) {
            log.info("delete=============");
        }

    }

}
