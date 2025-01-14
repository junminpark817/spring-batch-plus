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

package com.navercorp.spring.batch.plus.sample.step.chunkorientedstep.config.stepoperation

import com.navercorp.spring.batch.plus.kotlin.configuration.BatchDsl
import org.springframework.batch.core.Job
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.repeat.RepeatCallback
import org.springframework.batch.repeat.RepeatOperations
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.repeat.support.RepeatTemplate
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TestJobConfig {

    @Bean
    open fun testJob(
        batch: BatchDsl,
    ): Job = batch {
        job("testJob") {
            step("testStep") {
                chunk<Int, Int>(3, ResourcelessTransactionManager()) {
                    reader(testItemReader())
                    writer(testItemWriter())
                    stepOperations(
                        object : RepeatOperations {
                            override fun iterate(callback: RepeatCallback): RepeatStatus {
                                val delegate = RepeatTemplate()
                                println("custom iterate")
                                return delegate.iterate(callback)
                            }
                        },
                    )
                }
            }
        }
    }

    @Bean
    open fun testItemReader(): ItemReader<Int> {
        return object : ItemReader<Int> {
            private var count = 0

            override fun read(): Int? {
                return if (count < 20) {
                    count++
                } else {
                    null
                }
            }
        }
    }

    @Bean
    open fun testItemWriter(): ItemWriter<Int> {
        return ItemWriter { items ->
            println("[${Thread.currentThread().name}] write $items")
        }
    }
}
