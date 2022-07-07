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

package com.navercorp.spring.batch.plus.item;

import java.util.Objects;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.lang.NonNull;

/**
 * An adaptor factory for {@link ItemStreamReaderDelegate}, {@link ItemProcessorDelegate}, {@link ItemStreamWriterDelegate}.
 *
 * @since 0.1.0
 */
public final class AdaptorFactory {

	/**
	 * Create an adaptor which adapt {@link ItemStreamReaderDelegate} to {@link ItemStreamReader}.
	 *
	 * @param delegate a delegate
	 * @return an adapted ItemStreamReader
	 */
	public static <T> ItemStreamReader<T> itemStreamReader(@NonNull ItemStreamReaderDelegate<T> delegate) {
		Objects.requireNonNull(delegate, "ItemStreamReader delegate is null");
		return StepScopeItemStreamReader.withDelegate(() -> ItemStreamReaderAdaptor.withDelegate(delegate));
	}

	/**
	 * Create an adaptor which adapt {@link ItemProcessorDelegate} to {@link ItemProcessor}.
	 *
	 * @param delegate a delegate
	 * @return an adapted ItemProcessor
	 */
	public static <I, O> ItemProcessor<I, O> itemProcessor(@NonNull ItemProcessorDelegate<I, O> delegate) {
		Objects.requireNonNull(delegate, "ItemProcessor delegate is null");
		return ItemProcessorAdaptor.withDelegate(delegate);
	}

	/**
	 * Create an adaptor which adapt {@link ItemStreamWriterDelegate} to {@link ItemStreamWriter}.
	 *
	 * @param delegate a delegate
	 * @return an adapted ItemStreamWriter
	 */
	public static <T> ItemStreamWriter<T> itemStreamWriter(@NonNull ItemStreamWriterDelegate<T> delegate) {
		Objects.requireNonNull(delegate, "ItemStreamWriter delegate is null");
		return ItemStreamWriterAdaptor.withDelegate(delegate);
	}

	private AdaptorFactory() {
	}
}