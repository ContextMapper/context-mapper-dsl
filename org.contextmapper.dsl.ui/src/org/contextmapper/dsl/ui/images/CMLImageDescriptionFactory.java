/*
 * Copyright 2020 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.dsl.ui.images;

import java.net.URL;

import org.contextmapper.dsl.ui.internal.DslActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

/**
 * Factory method to create Context Mapper Logo as ImageDescriptor.
 * 
 * @author Stefan Kapferer
 *
 */
public class CMLImageDescriptionFactory {

	public static ImageDescriptor createContextMapperLogoDescriptor() {
		return ImageDescriptor.createFromURL(getIconURL("cml.png"));
	}

	public static ImageDescriptor createContextMapperLogo4DialogDescriptor() {
		return ImageDescriptor.createFromURL(getIconURL("cml-dialog-image.png"));
	}

	private static URL getIconURL(String icon) {
		Bundle bundle = Platform.getBundle(DslActivator.PLUGIN_ID);
		return bundle.getEntry("icons/" + icon);
	}

}
