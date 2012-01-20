/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.api.context.model.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
	
/**
 * This helper class is used to serialise and deserialise objects.
 * <p>
 * The {@link #serialise(Serializable)} method can be used to serialise an object
 * into a byte array as follows:
 * <pre>
 * // Assuming FooClass implements Serializable
 * FooClass fooObject;
 * byte[] fooBytes;
 * try {
 *     fooBytes = SerializationHelper.serialize(fooObject);
 * } catch (IOException ioe) {
 *     // Do handle the exception!
 * }
 * </pre>
 * The <code>FooClass</code> object can be deserialised from the byte array 
 * using the {@link #deserialise(byte[], ClassLoader)} method as
 * follows:
 * <pre>
 * try {
 *     fooObject = SerializationHelper.deserialize(fooBytes, this.getClass().getClassLoader());
 * } catch (IOException ioe) {
 *     // Do handle the exception!
 * } catch (ClassNotFoundException cnfe) {
 *     // Do handle the exception!
 * }
 * </pre> 
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class SerialisationHelper {

	/**
	 * Serialises the specified object into a byte array
	 * 
	 * @param object
	 *            the object to serialise
	 * @return a byte array of the serialised object
	 * @throws IOException if the serialisation of the specified object fails
	 */
	public static byte[] serialise(Serializable object) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		
		return baos.toByteArray();
	}

	/**
	 * Deserialises an object from the specified byte array
	 * 
	 * @param objectData
	 *            the object to deserialise
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for deserialisation
	 * @return the deserialised object
	 * @throws IOException if the deserialisation of the specified byte array fails
	 * @throws ClassNotFoundException if the class of the deserialised object cannot be found
	 */
	public static Serializable deserialise(byte[] objectData,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {

		CustomObjectInputStream ois = new CustomObjectInputStream(
				new ByteArrayInputStream(objectData), classLoader);

		return (Serializable) ois.readObject();
	}

	/**
	 * Credits go to jboss/hibernate for the inspiration
	 */
	private static final class CustomObjectInputStream extends ObjectInputStream {

		// The ClassLoader to use for deserialisation
		private ClassLoader classLoader;

		public CustomObjectInputStream(InputStream is, ClassLoader cl)
				throws IOException {
			super(is);
			this.classLoader = cl;
		}

		protected Class<?> resolveClass(ObjectStreamClass clazz)
				throws IOException, ClassNotFoundException {

			String className = clazz.getName();
			Class<?> resolvedClass = null;

			//System.out.println("[DEBUG]" + "Attempting to resolve class " + className);
			try {
				resolvedClass = this.classLoader.loadClass(className);
				//System.out.println("[DEBUG]" + className	+ " resolved through specified class loader");
			} catch (ClassNotFoundException e) {
				//System.out.println("[DEBUG]" + "Asking parent class to resolve " + className);
				resolvedClass = super.resolveClass(clazz);
			}

			return resolvedClass;
		}
	}
}