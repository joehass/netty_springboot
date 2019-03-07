/**
 * Copyright 2012 José Martínez
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.weaver.netty.stanza;

import com.google.common.base.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

/**
 * Defines a XMPP JID.
 * 
 * <code>[ node "@" ] domain [ "/" resource ]</code>
 * 
 * @see <a href="http://xmpp.org/rfcs/rfc6122.html">RFC 6122</a>
 */
@Immutable
public final class JID implements Serializable {


	private final String domain;
	private final String node;
	private final String resource;

	/**
	 * Parse a string and return a JID.
	 * 
	 * @param uri
	 *            the string to be parsed
	 * @return a JID if it is a valid JID string, {@code null} otherwise
	 */
	@Nullable
	public static final JID jid(@Nullable final String uri) {
		if (uri == null || uri.isEmpty())
			return null;

		String node = null;
		String domain = null;
		String resource = null;

		final int atIndex = uri.indexOf('@');
		final int barIndex = uri.indexOf('/', atIndex + 1);
		if (atIndex == 0)
			throw new IllegalArgumentException("Node cannot be empty");
		if (barIndex > 0 && barIndex - atIndex <= 1)
			throw new IllegalArgumentException("Domain cannot be empty");
		if (barIndex == uri.length() - 1)
			throw new IllegalArgumentException("Resource cannot be empty");

		if (atIndex > 0) {
			node = uri.substring(0, atIndex);
			if (barIndex > atIndex + 1) {
				domain = uri.substring(atIndex + 1, barIndex);
				resource = uri.substring(barIndex + 1);
			}
			else if (barIndex < 0) {
				domain = uri.substring(atIndex + 1);
			}
		}
		else if (atIndex < 0) {
			if (barIndex > 0) {
				domain = uri.substring(0, barIndex);
				resource = uri.substring(barIndex + 1);
			}
			else if (barIndex < 0) {
				domain = uri.substring(0);
			}
		}

		return new JID(domain, node, resource);
	}

	/**
	 * Create a new JID object with the given attributes.
	 * 
	 * @param domain
	 *            the domain of the JID
	 * @param node
	 *            the node of the JID
	 * @param resource
	 *            the resource of the JID
	 * @return a JID object
	 */
	public static final JID jid(final String node,final String domain, @Nullable final String resource) {
		final JID result = new JID(node, domain, resource);
		return result;
	}

	private JID(final String node,final String domain,@Nullable final String resource) {
		this.domain = domain;
		this.node = node;
		this.resource = resource;
	}

	/**
	 * Returns the bare JID for this JID (the JID without resource).
	 * 
	 * @return the bare JID for this JID
	 */
	public final JID getBareJID() {
		return new JID(domain, node, null);
	}

	/**
	 * Returns the domain for this JID.
	 * 
	 * @return the domain for this JID
	 */
	public final String getDomain() {
		return domain;
	}

	/**
	 * Returns the node for this JID.
	 * 
	 * @return the node for this JID
	 */
	@Nullable
	public final String getNode() {
		return node;
	}

	/**
	 * Returns the resource for this JID.
	 * 
	 * @return the resource for this JID
	 */
	@Nullable
	public final String getResource() {
		return resource;
	}

	@Override
	public final int hashCode() {
		return Objects.hashCode(domain, node, resource);
	}

    @Override
    public String toString() {
        return node +"@" + domain + "/" + resource;
    }
}
