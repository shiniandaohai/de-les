package com.boer.delos;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    class User{
        private String name;
        public User(String name){
            this.name=name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Test
    public void testList(){
        User user1=new User("1");
        User user2=new User("2");
        User user3=new User("3");
        User user4=new User("4");

        List<User> users1=new ArrayList<>();
        users1.add(user1);
        users1.add(user2);

        List<User> users2=new ArrayList<>();
        users2.add(user1);
        users2.add(user2);
        users2.add(user3);
        users2.add(user4);

        users2.removeAll(users1);
        System.out.println(users2.toString());
        System.out.println(users1.toString());
    }

    @Test
    public void testTime(){

        computeRevertMillis("1505728363");
    }

    public String computeRevertMillis(String second) {


        //转为毫秒
        long ms = Long.parseLong(second) * 1000 - System.currentTimeMillis();

        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute=(ms-day*dd-hour*hh)/mi;

        StringBuffer sb = new StringBuffer();
        if (day >= 0) {
            sb.append(day);
        }
        if (hour >= 0) {
            sb.append(hour);
        }

        if(minute>=0){
            sb.append(minute+"分");
        }

        return sb.toString();


    }

    @Test
    public void testCalendar(){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        System.out.println(maxDate);
    }

    @Test
    public void testJson(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("kk","moon");
            System.out.println(jsonObject);
            jsonObject.put("kk","moon1");
            System.out.println(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}