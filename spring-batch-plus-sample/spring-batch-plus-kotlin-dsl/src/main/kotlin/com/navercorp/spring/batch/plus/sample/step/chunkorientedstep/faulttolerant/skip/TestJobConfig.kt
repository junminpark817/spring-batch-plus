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

package com.navercorp.spring.batch.plus.sample.step.chunkorientedstep.faulttolerant.skip

import com.navercorp.spring.batch.plus.kotlin.configuration.BatchDsl
import org.springframework.batch.core.Job
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TestJobConfig {

    @Bean
    open fun testJob(batch: BatchDsl): Job = batch {
        job("testJob") {
            step("testStep") {
                chunk<Int, String>(3, ResourcelessTransactionManager()) {
                    reader(testItemReader())
                    processor(testItemProcessor())
                    writer(testItemWriter())
                    faultTolerant {
                        skip<RuntimeException>()
                        skipLimit(4)
                    }
                }
            }
        }
    }

    @Bean
    open fun testItemReader(): ItemReader<Int> {
        return object : ItemReader<Int> {
            private var count = 0

            override fun read(): Int? {
                if (count < 11) {
                    return count++
                } else {
                    return null
                }
            }
        }
    }

    @Bean
    open fun testItemProcessor(): ItemProcessor<Int, String> {
        return ItemProcessor<Int, String> { item ->
            if (item % 3 == 0) {
                throw IllegalStateException("Error")
            }

            item.toString()
        }
    }

    @Bean
    open fun testItemWriter(): ItemWriter<String> {
        return ItemWriter { items ->
            println("write $items")
        }
    }
}
