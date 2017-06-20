package org.main.recap.batch.job;

import org.main.recap.batch.service.PurgeEmailAddressService;
import org.main.recap.batch.service.UpdateJobDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * Created by rajeshbabuk on 28/3/17.
 */
public class PurgeEmailAddressTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(PurgeEmailAddressTasklet.class);

    @Value("${scsb.solr.client.url}")
    String solrClientUrl;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Autowired
    private PurgeEmailAddressService purgeEmailAddressService;

    @Autowired
    private UpdateJobDetailsService updateJobDetailsService;

    /**
     * This method starts the execution of purging email addresses job.
     * @param contribution
     * @param chunkContext
     * @return
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info("Executing PurgeEmailAddressTasklet");
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        long jobInstanceId = jobExecution.getJobInstance().getInstanceId();
        String jobName = jobExecution.getJobInstance().getJobName();
        Date createdDate = jobExecution.getCreateTime();
        updateJobDetailsService.updateJob(solrClientUrl, jobName, createdDate, jobInstanceId);

        String status = purgeEmailAddressService.purgeEmailAddress(scsbCircUrl);
        logger.info("Purge Email Address status : {}", status);
        return RepeatStatus.FINISHED;
    }
}
