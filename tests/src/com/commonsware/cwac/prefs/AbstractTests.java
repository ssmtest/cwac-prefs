/***
  Copyright (c) 2012 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.cwac.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.test.AndroidTestCase;
import java.util.HashSet;
import java.util.Set;
import com.commonsware.cwac.prefs.CWSharedPreferences.StorageStrategy;

abstract public class AbstractTests extends AndroidTestCase {
  abstract protected StorageStrategy getMainStrategy();
  abstract protected StorageStrategy getTestStrategy();
  abstract protected void deleteDatabase(Context ctxt);
  
  SharedPreferences prefs=null;
  boolean listenerTriggered=false;
  StorageStrategy mainStrategy=null;

  protected void setUp() throws Exception {
    super.setUp();
  
    mainStrategy=getMainStrategy();
    prefs=new CWSharedPreferences(mainStrategy);
  }

  protected void tearDown() throws Exception {
    mainStrategy.close();
    deleteDatabase(getContext());
  
    super.tearDown();
  }

  @TargetApi(11)
  public void testDefaults() {
    assertTrue(prefs.getBoolean("foo", true));
    assertFalse(prefs.getBoolean("foo", false));
    assertEquals(prefs.getFloat("bar", 0.0f), 0.0f);
    assertEquals(prefs.getFloat("bar", 1.2f), 1.2f);
    assertEquals(prefs.getFloat("bar", -3.4f), -3.4f);
    assertEquals(prefs.getInt("goo", 0), 0);
    assertEquals(prefs.getInt("goo", 5), 5);
    assertEquals(prefs.getInt("goo", -7), -7);
    assertEquals(prefs.getLong("baz", 0L), 0L);
    assertEquals(prefs.getLong("baz", 9L), 9L);
    assertEquals(prefs.getLong("baz", -11L), -11L);
    assertNull(prefs.getString("iNeedMoreFakeKeys", null));
    assertEquals("This is a test",
                 prefs.getString("iNeedMoreFakeKeys", "This is a test"));
  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      Set<String> input=new HashSet<String>();
  
      assertEquals(prefs.getStringSet("ooooAnother!", input).size(), 0);
  
      input.add("foo");
      input.add("bar");
      input.add("goo");
      input.add("baz");
  
      Set<String> output=prefs.getStringSet("ooooAnother!", input);
  
      assertEquals(output.size(), 4);
      assertTrue(output.containsAll(input));
    }
  }
  
  @TargetApi(11)
  public void testCacheChanges() {
    prefs.edit().putBoolean("foo", true).commit();
    assertTrue(prefs.getBoolean("foo", false));

    prefs.edit().putFloat("bar", 2.0f).commit();
    assertEquals(prefs.getFloat("bar", 0.0f), 2.0f);

    prefs.edit().putInt("baz", 3).commit();
    assertEquals(prefs.getInt("baz", 0), 3);

    prefs.edit().putLong("goo", 4L).commit();
    assertEquals(prefs.getLong("goo", 0L), 4L);

    prefs.edit().putString("iNeedMoreFakeKeys", "value").commit();
    assertEquals(prefs.getString("iNeedMoreFakeKeys", null), "value");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      Set<String> input=new HashSet<String>();

      input.add("foo");
      input.add("bar");
      input.add("goo");
      input.add("baz");

      prefs.edit().putStringSet("ooooAnother!", input).commit();

      Set<String> output=prefs.getStringSet("ooooAnother!", null);

      assertEquals(output.size(), 4);
      assertTrue(output.containsAll(input));
    }
  }

  @TargetApi(11)
  public void testFailedCommits() {
    prefs.edit().putBoolean("foo", true);
    assertFalse(prefs.getBoolean("foo", false));

    prefs.edit().putFloat("bar", 2.0f);
    assertEquals(prefs.getFloat("bar", 0.0f), 0.0f);

    prefs.edit().putInt("baz", 3);
    assertEquals(prefs.getInt("baz", 0), 0);

    prefs.edit().putLong("goo", 4L);
    assertEquals(prefs.getLong("goo", 4L), 4L);

    prefs.edit().putString("iNeedMoreFakeKeys", "value");
    assertNull(prefs.getString("iNeedMoreFakeKeys", null));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      Set<String> input=new HashSet<String>();

      input.add("foo");
      input.add("bar");
      input.add("goo");
      input.add("baz");

      prefs.edit().putStringSet("ooooAnother!", input);

      Set<String> output=prefs.getStringSet("ooooAnother!", null);

      assertNull(output);
    }
  }
  
  @TargetApi(11)
  public void testPersistentChanges() {
    StorageStrategy testStrategy=getTestStrategy();

    prefs.edit().putBoolean("foo", true).commit();
    assertTrue(new CWSharedPreferences(testStrategy).getBoolean("foo",
                                                                false));

    prefs.edit().putFloat("bar", 2.0f).commit();
    assertEquals(new CWSharedPreferences(testStrategy).getFloat("bar",
                                                                0.0f),
                 2.0f);

    prefs.edit().putInt("baz", 3).commit();
    assertEquals(new CWSharedPreferences(testStrategy).getInt("baz", 0),
                 3);

    prefs.edit().putLong("goo", 4L).commit();
    assertEquals(new CWSharedPreferences(testStrategy).getLong("goo",
                                                               0L), 4L);

    prefs.edit().putString("iNeedMoreFakeKeys", "value").commit();
    assertEquals(new CWSharedPreferences(testStrategy).getString("iNeedMoreFakeKeys",
                                                                 null),
                 "value");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      Set<String> input=new HashSet<String>();

      input.add("foo");
      input.add("bar");
      input.add("goo");
      input.add("baz");

      prefs.edit().putStringSet("ooooAnother!", input).commit();

      Set<String> output=
          new CWSharedPreferences(testStrategy).getStringSet("ooooAnother!",
                                                             null);

      assertEquals(output.size(), 4);
      assertTrue(output.containsAll(input));
    }

    testStrategy.close();
  }

  public void testListener() {
    final StorageStrategy testStrategy=getTestStrategy();

    prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                            String key) {
        listenerTriggered=true;
        assertTrue(new CWSharedPreferences(testStrategy).getBoolean("foo",
                                                                    false));
      }
    });

    prefs.edit().putBoolean("foo", true).commit();
    assertTrue(listenerTriggered);

    testStrategy.close();
  }

}