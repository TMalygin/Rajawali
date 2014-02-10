/**
 * Copyright 2013 Dennis Ippel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package rajawali.parser;

import java.io.File;

import rajawali.Object3D;
import rajawali.materials.textures.TextureManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

public abstract class AMeshLoader extends ALoader implements IMeshLoader {

	private TextureManager mTextureManager;

	protected Object3D mRootObject = new Object3D();

	// constructors -------------------------------------------------
	public AMeshLoader(File file) {
		super(file);
	}

	public AMeshLoader(String fileOnSDCard) {
		super(fileOnSDCard);
	}

	public AMeshLoader(AssetManager asset, String filepath) {
		super(asset, filepath);
	}

	public AMeshLoader(Resources resources, int resourceId) {
		super(resources, resourceId);
	}

	// ~constructors -------------------------------------------------
	public void setTextureManager(TextureManager textureManager) {
		this.mTextureManager = textureManager;
	}

	public TextureManager getTextureManager() {
		return mTextureManager;
	}

	public Object3D getParsedObject() {
		return mRootObject;
	}

	protected class MaterialDef {

		public String name;
		public int ambientColor;
		public int diffuseColor;
		public int specularColor;
		public float specularCoefficient;
		public float alpha;
		public String ambientTexture;
		public String diffuseTexture;
		public String specularColorTexture;
		public String specularHighlightTexture;
		public String alphaTexture;
		public String bumpTexture;
	}
}
