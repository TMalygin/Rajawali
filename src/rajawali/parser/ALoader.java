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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import rajawali.util.RajLog;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

public abstract class ALoader implements ILoader {

	public static enum Type {
		RAW, SDCARD, ASSETS
	}

	private Type mType;

	// raw
	private Resources mResources = null;
	private int mResourceId = 0;
	// sdcard
	private String mFileOnSDCard = null;
	private File mFile = null;
	// assets
	private String mAssetFileName = null;
	private AssetManager mAssets = null;

	// abstract methods ---------------------------------------------
	protected abstract void parse(InputStream is) throws ParsingException;

	// ~abstract methods --------------------------------------------

	// constructors -------------------------------------------------
	public ALoader(File file) {
		this(file.getAbsolutePath());
		this.mFile = file;
	}

	public ALoader(String fileOnSDCard) {
		this.mFileOnSDCard = fileOnSDCard;
		this.mType = Type.SDCARD;
	}

	public ALoader(AssetManager assets, String filename) {
		this.mAssets = assets;
		this.mAssetFileName = filename;
		this.mType = Type.ASSETS;
	}

	public ALoader(Resources resources, int resourceId) {
		this.mResources = resources;
		this.mResourceId = resourceId;
		this.mType = Type.RAW;
	}

	// ~constructors ------------------------------------------------
	// getter/setter ------------------------------------------------
	public void setResources(Resources resources) {
		this.mResources = resources;
	}

	public Type getType() {
		return mType;
	}

	public Resources getResources() {
		return mResources;
	}

	public int getResourceId() {
		return mResourceId;
	}

	public AssetManager getAssets() {
		return mAssets;
	}

	public String getFilepath() {
		switch (mType) {
		case ASSETS:
			return mAssetFileName;
		case SDCARD:
			return mFileOnSDCard;
		default:
			return null;
		}
	}

	public String getParentFolder() {
		switch (mType) {
		case ASSETS:
			// TODO: getParent!!
			int lastPos = mAssetFileName.lastIndexOf(File.separator);
			return lastPos == -1 ? "" : mAssetFileName.substring(0, lastPos);
		case SDCARD:
			if (mFile == null) {
				mFile = new File(Environment.getExternalStorageDirectory(), mFileOnSDCard);
			}
			return mFile.getParent();
		default:
			return null;
		}
	}

	// ~getter/setter -----------------------------------------------
	public final ILoader parse() throws ParsingException {
		InputStream is = null;
		Throwable thr = null;

		try {
			is = getInputStream();
			parse(is);
		} catch (Exception e) {
			RajLog.e("parse error", e);
			thr = e;
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (Exception e) {}
			}
		}

		if (thr != null) {
			throw new ParsingException(thr);
		}
		return this;
	}

	public InputStream getInputStream() throws FileNotFoundException, IOException, ParsingException {
		InputStream is = null;
		switch (mType) {
		case SDCARD:
			if (mFile == null) {
				mFile = new File(Environment.getExternalStorageDirectory(), mFileOnSDCard);
			}
			is = new FileInputStream(mFile);
			break;
		case RAW:
			is = mResources.openRawResource(mResourceId);
			break;
		case ASSETS:
			is = mAssets.open(mAssetFileName);
			break;
		default:
			throw new ParsingException("unknown type");
		}
		return is;
	}

	protected String readString(InputStream stream) throws IOException {
		String result = new String();
		byte inByte;
		while ((inByte = (byte) stream.read()) != 0)
			result += (char) inByte;
		return result;
	}

	protected int readInt(InputStream stream) throws IOException {
		return stream.read() | (stream.read() << 8) | (stream.read() << 16)
				| (stream.read() << 24);
	}

	protected int readShort(InputStream stream) throws IOException {
		return (stream.read() | (stream.read() << 8));
	}

	protected float readFloat(InputStream stream) throws IOException {
		return Float.intBitsToFloat(readInt(stream));
	}

	protected String getOnlyFileName(String fileName) {
		String fName = new String(fileName);
		int indexOf = fName.lastIndexOf("\\");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		indexOf = fName.lastIndexOf("/");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		return fName.toLowerCase(Locale.ENGLISH).replaceAll("\\s", "_");
	}

	protected String getFileNameWithoutExtension(String fileName) {
		String fName = fileName.substring(0, fileName.lastIndexOf("."));
		int indexOf = fName.lastIndexOf("\\");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		indexOf = fName.lastIndexOf("/");
		if (indexOf > -1)
			fName = fName.substring(indexOf + 1, fName.length());
		return fName.toLowerCase(Locale.ENGLISH).replaceAll("\\s", "_");
	}

}
