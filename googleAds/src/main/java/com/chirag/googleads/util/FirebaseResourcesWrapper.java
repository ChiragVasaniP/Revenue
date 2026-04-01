/*
package com.chirag.googleads.util;

import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

*/
/**
 * Subclass of android.content.res.Resources that is used to capture Firebase Analytics
 * initialization process to return the "google_app_id" string that we require, programmatically,
 * rather than it being embedded into strings.xml.
 * <p>
 * All visible (e.g. not marked @hide) public methods from Resources class (API v32) are
 * replicated here, and call the wrapped Resources instance, except the getIdentifier() and
 * getString() methods.
 *//*


public class FirebaseResourcesWrapper extends Resources {

    private static final String GOOGLE_APP_ID = "google_app_id";
    private static final int R_STRING_GOOGLE_APP_ID = 1_999_999_999;


    private static final String APP_ID = "1:1062841038778:android:202ee79a23b33248ec8f83";

    private final Resources wrapped;

    public FirebaseResourcesWrapper(Resources wrapped) {
        super(wrapped.getAssets(), wrapped.getDisplayMetrics(), wrapped.getConfiguration());
        this.wrapped = wrapped;
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        // 1. Firebase Analytics calls getIdentifier("google_app_id", "String", "<package>")
        // first in order to get the R.string.google_app_id value that would have been
        // generated, instead we return R_STRING_GOOGLE_APP_ID. (We don't have an entry in
        // strings.xml).
        if (GOOGLE_APP_ID.equals(name) && "string".equals(defType))
            return R_STRING_GOOGLE_APP_ID;
        return wrapped.getIdentifier(name, defType, defPackage);
    }

    @Override
    public String getString(int id) throws NotFoundException {
        // 2. Firebase Analytics takes the R_STRING_GOOGLE_APP_ID value returned from
        // getIdentifier() above and then calls this getString() method to return the actual value.
        if (id == R_STRING_GOOGLE_APP_ID && getResourcePackageName())
            return APP_ID;
        return wrapped.getString(id);
    }

    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return wrapped.getString(id, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs)
            throws NotFoundException {
        return wrapped.getQuantityString(id, quantity, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return wrapped.getQuantityString(id, quantity);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return wrapped.getText(id, def);
    }

    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        return wrapped.getTextArray(id);
    }

    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        return wrapped.getStringArray(id);
    }

    @Override
    public Typeface getFont(int id) throws NotFoundException {
        return wrapped.getFont(id);
    }

    @Override
    public CharSequence getQuantityText(int id, int quantity)
            throws NotFoundException {
        return wrapped.getQuantityText(id, quantity);
    }

    @Override
    public int[] getIntArray(int id) throws NotFoundException {
        return wrapped.getIntArray(id);
    }

    @Override
    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        return wrapped.obtainTypedArray(id);
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        return wrapped.getDimension(id);
    }

    @Override
    public int getDimensionPixelOffset(int id) throws NotFoundException {
        return wrapped.getDimensionPixelOffset(id);
    }

    @Override
    public int getDimensionPixelSize(int id) throws NotFoundException {
        return wrapped.getDimensionPixelOffset(id);
    }

    @Override
    public float getFraction(int id, int base, int pbase) {
        return wrapped.getFraction(id, base, pbase);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        return wrapped.getDrawable(id);
    }

    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        return wrapped.getDrawable(id, theme);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        return wrapped.getDrawableForDensity(id, density);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        return wrapped.getDrawableForDensity(id, density, theme);
    }

    @Override
    public Movie getMovie(int id) throws NotFoundException {
        return wrapped.getMovie(id);
    }

    @Override
    @Deprecated
    public int getColor(int id) throws NotFoundException {
        return wrapped.getColor(id);
    }

    @Override
    public int getColor(int id, Theme theme) throws NotFoundException {
        return wrapped.getColor(id, theme);
    }

    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        return wrapped.getColorStateList(id);
    }

    @Override
    public ColorStateList getColorStateList(int id, Theme theme) throws NotFoundException {
        return wrapped.getColorStateList(id, theme);
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        return wrapped.getBoolean(id);
    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        return wrapped.getInteger(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public float getFloat(int id) {
        return wrapped.getFloat(id);
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return wrapped.getLayout(id);
    }

    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return wrapped.getAnimation(id);
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        return wrapped.getXml(id);
    }

    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        return wrapped.openRawResource(id);
    }

    @Override
    public InputStream openRawResource(int id, TypedValue value)
            throws NotFoundException {
        return wrapped.openRawResource(id, value);
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(int id)
            throws NotFoundException {
        return wrapped.openRawResourceFd(id);
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs)
            throws NotFoundException {
        wrapped.getValue(id, outValue, resolveRefs);
    }

    @Override
    public void getValueForDensity(int id, int density, TypedValue outValue,
                                   boolean resolveRefs) throws NotFoundException {
        wrapped.getValueForDensity(id, density, outValue, resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs)
            throws NotFoundException {
        wrapped.getValue(name, outValue, resolveRefs);
    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        return wrapped.obtainAttributes(set, attrs);
    }

    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        wrapped.updateConfiguration(config, metrics);
    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        return wrapped.getDisplayMetrics();
    }

    @Override
    public Configuration getConfiguration() {
        return wrapped.getConfiguration();
    }

    @Override
    public String getResourceName(int resid) throws NotFoundException {
        return wrapped.getResourceName(resid);
    }

    @Override
    public String getResourcePackageName(int resid) throws NotFoundException {
        return wrapped.getResourcePackageName(resid);
    }

    @Override
    public String getResourceTypeName(int resid) throws NotFoundException {
        return wrapped.getResourceTypeName(resid);
    }

    @Override
    public String getResourceEntryName(int resid) throws NotFoundException {
        return wrapped.getResourceEntryName(resid);
    }

    @Override
    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle)
            throws XmlPullParserException, IOException {
        wrapped.parseBundleExtras(parser, outBundle);
    }

    @Override
    public void parseBundleExtra(String tagName, AttributeSet attrs,
                                 Bundle outBundle) throws XmlPullParserException {
        wrapped.parseBundleExtra(tagName, attrs, outBundle);
    }

}*/
