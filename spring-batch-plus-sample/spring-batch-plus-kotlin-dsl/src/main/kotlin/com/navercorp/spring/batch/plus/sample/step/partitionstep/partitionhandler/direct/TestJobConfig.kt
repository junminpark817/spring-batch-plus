/*
 * Spring Batch Plus
 *
 * Copyright 2022-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.spring.batch.plus.sample.step.partitionstep.partitionhandler.direct

import com.navercorp.spring.batch.plus.kotlin.configuration.BatchDsl
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor

@Configuration
open class TestJobConfig(
    private val batch: BatchDsl,
) {

    @Bean
    open fun testJob(): Job = batch {
        job("testJob") {
            step("testStep") {
                partitioner {
                    splitter("workerStep") { gridSize ->
                        (0 until gridSize).associate {
                            "partition-$it" to ExecutionContext()
                        }
                    }
                    partitionHandler(
                        TaskExecutorPartitionHandler().apply {
                            setTaskExecutor(SimpleAsyncTaskExecutor())
                            step = actualStep()
                            gridSize = 4
                        },
                    )
                }
            }
        }
    }

    @Bean
    open fun actualStep(): Step = batch {
        step("actualStep") {
            tasklet(
                { contribution, _ ->
                    println("[${Thread.currentThread().name}][${contribution.stepExecution.stepName}] run actual tasklet")
                    RepeatStatus.FINISHED
                },
                ResourcelessTransactionManager(),
            )
        }
    }
}
