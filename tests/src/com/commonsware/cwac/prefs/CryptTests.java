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

import android.content.Context;
import com.commonsware.cwac.prefs.CWSharedPreferences.LoadPolicy;
import com.commonsware.cwac.prefs.CWSharedPreferences.StorageStrategy;

public class CryptTests extends AbstractTests {
  private static final String NAME="com.commonsware.cwac.prefs.test2";

  @Override
  protected StorageStrategy getMainStrategy() {
    return(new SQLCipherStrategy(getContext(), NAME, "atestpassword", LoadPolicy.SYNC));
  }

  @Override
  protected StorageStrategy getTestStrategy() {
    return(getMainStrategy());
  }

  protected void deleteDatabase(Context ctxt) {
    ctxt.getDatabasePath(NAME).delete();
  }
}
