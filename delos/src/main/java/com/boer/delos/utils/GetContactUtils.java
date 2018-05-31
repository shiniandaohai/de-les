package com.boer.delos.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * @author wangkai
 * @Description: 获取联系人
 * create at 2015/12/1 21:24
 */
public class GetContactUtils {

    public static String getContact(Context mContext) {
        String phoneNumber = "";
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor phones = null;
        int contactIdIndex = 0;
        int nameIndex = 0;
//        List<MemberPhoneContact> list = new ArrayList<>();

        try {
            if (cursor.getCount() > 0) {
                contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            }

            while (cursor.moveToNext()) {
//                MemberPhoneContact data = new MemberPhoneContact();
//                data.setMemberId(memberId);
                String contactId = cursor.getString(contactIdIndex);
                String name = cursor.getString(nameIndex);
//                data.setContactName(name);
//                data.setContactPhone("");
            /*
             * 查找该联系人的phone信息
             */
                phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                        null, null);
                int phoneIndex = 0;
                if (phones.getCount() > 0) {
                    phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                }
                while (phones.moveToNext()) {
                    if (!StringUtil.isEmpty(phones.getString(phoneIndex))) {
                        phoneNumber = phones.getString(phoneIndex).replace(" ", "");
                    }
//                    data.setContactPhone(phoneNumber);
                }
//                if (!StringUtil.isEmpty(data.getContactPhone())) {
//                    list.add(data);
//                }
                phones.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return phoneNumber;
    }
}
