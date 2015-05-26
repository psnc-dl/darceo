/**
 * Copyright 2015 Poznań Supercomputing and Networking Center
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.psnc.synat.wrdz.common.utility;

import java.util.Map;

/**
 * Builder for overridden {@link Object#toString()} methods in WRDZ classes that provides convenient for logging layout
 * of string values.
 */
public class FormattedToStringBuilder {

    /**
     * String builder used by this builder to construct <code>toString</code> method value.
     */
    private final StringBuilder builder;


    /**
     * Creates new instance of formatted <code>toString</code> builder for the specified class name.
     * 
     * @param className
     *            name of the class serialized to string.
     */
    public FormattedToStringBuilder(String className) {
        builder = new StringBuilder(className);
        builder.append(" [");
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, Object value) {
        appendFieldName(fieldName);
        String valueOf = String.valueOf(value);
        builder.append(valueOf.replaceAll("\n", "\n\t"));
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed {@link Iterable} field to the <code>toString</code> method result. Will
     * format to provide correct indentation of nested values.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the {@link Iterable} field to be serialized.
     * @return current state of this builder.
     */
    @SuppressWarnings("rawtypes")
    public FormattedToStringBuilder append(String fieldName, Iterable value) {
        appendFieldName(fieldName);
        if (value == null) {
            builder.append("null");
        } else {
            builder.append("[");
            int i = 0;
            for (Object object : value) {
                builder.append("\n\t\t");
                String valueOf = String.valueOf(object);
                builder.append(valueOf.replaceAll("\n", "\n\t\t"));
                i++;
            }
            if (i > 0) {
                builder.append(",\n\t");
            }
            builder.append("]");
        }
        return this;
    }


    /**
     * Appends name and value of the passed {@link Map} field to the <code>toString</code> method result. Will format to
     * provide correct indentation and representation of nested key-value pairs.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the {@link Map} field to be serialized.
     * @return current state of this builder.
     */
    @SuppressWarnings("rawtypes")
    public FormattedToStringBuilder append(String fieldName, Map value) {
        appendFieldName(fieldName);
        if (value == null) {
            builder.append("null");
        } else {
            builder.append("{");
            int i = 0;
            for (Object object : value.keySet()) {
                builder.append("\n\t\t[ ");
                String key = String.valueOf(object);
                builder.append(key.replaceAll("\n", "\n\t\t"));
                builder.append("=");
                String val = String.valueOf(value.get(object));
                builder.append(val.replaceAll("\n", "\n\t\t"));
                builder.append(" ]");
                i++;
            }
            if (i > 0) {
                builder.append("\n\t");
            }
            builder.append("}");
        }
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, String value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, StringBuffer value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, char[] value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, boolean value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, char value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, int value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, long value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, float value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Appends name and value of the passed field to the <code>toString</code> method result.
     * 
     * @param fieldName
     *            name of the field.
     * @param value
     *            value of the field to be serialized.
     * @return current state of this builder.
     */
    public FormattedToStringBuilder append(String fieldName, double value) {
        appendFieldName(fieldName);
        builder.append(value);
        builder.append(",");
        return this;
    }


    /**
     * Returns the resulting string representation of object.
     * 
     * @return string representation of object.
     */
    public String toString() {
        builder.append("\n]");
        return builder.toString();
    }


    /**
     * Appends the name of the field to the result string.
     * 
     * @param fieldName
     *            name of the appended field.
     */
    private void appendFieldName(String fieldName) {
        builder.append("\n\t");
        builder.append(fieldName);
        builder.append(" = ");
    }

}
