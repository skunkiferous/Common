/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blockwithme.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>Registry</code> is a generic registry.
 *
 * It is thread-safe, and can delegate/fallback to another registry.
 */
public class RegistryImpl<K,V> implements Registry<K,V> {
	/** Registered converters. */
	private final Map<K, V> registry = new HashMap<K, V>();

	/** Parent registry. */
	private final RegistryImpl<K,V> parent;

	/** The key validator */
	private final Validator<K> keyValidator;

	/** The value validator */
	private final Validator<V> valueValidator;

	/**
	 * Constructor, with optional parent.
	 * If a validator is null, and key or value respectively is accepted
	 */
	@SuppressWarnings("unchecked")
	public RegistryImpl(final Validator<K> theKeyValidator,
			final Validator<V> theValueValidator,
			final RegistryImpl<K,V> theParent) {
		keyValidator = (theKeyValidator == null) ? (Validator<K>) Validator.ANY : theKeyValidator;
		valueValidator = (theValueValidator == null) ? (Validator<V>) Validator.ANY : theValueValidator;
		parent = theParent;
	}

	/**
	 * Constructor, with optional parent.
	 * If a type is null, and key or value respectively is accepted
	 */
	@SuppressWarnings("unchecked")
	public RegistryImpl(final Class<K> theKeyType,
			final Class<V> theValueType,
			final RegistryImpl<K,V> theParent) {
		keyValidator = (theKeyType == null) ? (Validator<K>) Validator.ANY : new ExactyTypeValidator<>(theKeyType);
		valueValidator = (theValueType == null) ? (Validator<V>) Validator.ANY : new ExactyTypeValidator<>(theValueType);
		parent = theParent;
	}

	/**
	 * Constructor.
	 * If a validator is null, and key or value respectively is accepted
	 */
	public RegistryImpl(final Validator<K> theKeyValidator,
			final Validator<V> theValueValidator) {
		this(theKeyValidator, theValueValidator, null);
	}

	/**
	 * Constructor.
	 * If a type is null, and key or value respectively is accepted
	 */
	public RegistryImpl(final Class<K> theKeyType,
			final Class<V> theValueType) {
		this(theKeyType, theValueType, null);
	}

	/**
	 * Constructor, with optional parent.
	 */
	public RegistryImpl(final RegistryImpl<K,V> theParent) {
		this((Validator<K>) null, (Validator<V>) null, theParent);
	}

	/**
	 * Constructor.
	 */
	public RegistryImpl() {
		this((Validator<K>) null, (Validator<V>) null, null);
	}

	public V register(final K key, final V value, final boolean update) {
		final String checkKey = keyValidator.validate(key);
		if (checkKey != null) {
			throw new IllegalArgumentException("key "+key+" "+checkKey);
		}
		final String checkValue = valueValidator.validate(value);
		if (checkValue != null) {
			throw new IllegalArgumentException("value "+value+" "+checkValue);
		}
		synchronized (this) {
			final V result = find2(key);
			if ((result == null) || update) {
				registry.put(key, value);
			}
			return result;
		}
	}

	private V find2(final K key) {
		V result = registry.get(key);
		if ((result == null) && (parent != null)) {
			result = parent.find(key);
		}
		return result;
	}

	public synchronized V find(final K key) {
		return find2(key);
	}

	public V get(final K key) {
		final V result = find(key);
		if (result == null) {
			throw new IllegalStateException("value missing for key "+key);
		}
		return result;
	}
}