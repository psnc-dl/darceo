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
package pl.psnc.synat.dsa.util;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Provides static utility methods for objects associated with a transaction.
 * 
 */
public final class TransactionUtils {

    /**
     * Hexadecimal digits.
     */
    private static final String HEX_DIGITS = "0123456789ABCDEF";


    /**
     * Private constructor.
     */
    private TransactionUtils() {
    }


    /**
     * Returns a string representation of any part of transaction id.
     * 
     * @param id
     *            some part of transaction id
     * @return string representation
     */
    private static String bytesToString(byte[] id) {
        StringBuffer sb = new StringBuffer(128);
        int value;
        for (byte b : id) {
            value = b & 0xff;
            sb.append(HEX_DIGITS.charAt(value / 16));
            sb.append(HEX_DIGITS.charAt(value & 15));
        }
        return sb.toString();
    }


    /**
     * Returns xid id (global part and branch qualifier as a hex string).
     * 
     * @param xid
     *            xid
     * @return xid as hex string
     */
    public static String getXidAsString(Xid xid) {
        StringBuffer sb = new StringBuffer();
        sb.append(TransactionUtils.bytesToString(xid.getGlobalTransactionId()));
        sb.append("_");
        sb.append(TransactionUtils.bytesToString(xid.getBranchQualifier()));
        return sb.toString();
    }


    /**
     * Returns folder name for transaction based upon the xid.
     * 
     * @param xid
     *            xid
     * @return folder name for transaction
     */
    public static String getFolderNameForXid(Xid xid) {
        // this use the way as glassfish creates xid parts (global transaction
        // id repeats some part of branch qualifier)
        StringBuffer sb = new StringBuffer();
        String gtid = TransactionUtils.bytesToString(xid.getGlobalTransactionId());
        if (gtid.length() >= 16) {
            sb.append(gtid.substring(0, 15));
        } else {
            sb.append(gtid);
        }
        sb.append(TransactionUtils.bytesToString(xid.getBranchQualifier()));
        return sb.toString();
    }


    /**
     * Returns a string representation of transaction flags.
     * 
     * @param flags
     *            flags
     * @return string representation
     */
    public static String flagsToString(int flags) {
        switch (flags) {
            case XAResource.TMNOFLAGS:
                // no flags options are selected
                return "TMNOFLAGS";
            case XAResource.TMJOIN:
                // caller is joining existing transaction branch
                return "TMJOIN";
            case XAResource.TMRESUME:
                // caller is resuming association with a suspended transaction
                // branch
                return "TMRESUME";
            case XAResource.TMSUCCESS:
                // caller is dissociating from a transaction branch
                return "TMSUCCESS";
            case XAResource.TMFAIL:
                // caller is dissociating from a transaction branch and marks
                // the transaction branch rollback-only.
                return "TMFAIL";
            case XAResource.TMSUSPEND:
                // Caller is suspending (not ending) its association with a
                // transaction branch.
                return "TMSUSPEND";
            default:
                return String.valueOf(flags);
        }
    }

}
