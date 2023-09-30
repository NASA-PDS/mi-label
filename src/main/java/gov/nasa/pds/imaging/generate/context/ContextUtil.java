// Copyright 2020, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.imaging.generate.context;

import gov.nasa.pds.imaging.generate.TemplateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextUtil {

  private final List<Map<String, String>> objectList;
  private final Map<String, List<String>> elMap;
  private int elCnt;

  public ContextUtil() {
    this.objectList = new ArrayList<Map<String, String>>();
    this.elMap = new HashMap<String, List<String>>();
    this.elCnt = -1;
  }

  public void addDictionaryElement(final String key, final List<String> elList)
      throws TemplateException {
    final int currSize = elList.size();

    // Verify element count has been set, and is equal to previous element
    // lists
    if (this.elCnt == -1) {
      this.elCnt = elList.size();
    } else if (this.elCnt != currSize) {
      throw new TemplateException("Length of keyword lists must be equal");
    }

    this.elMap.put(cleanKey(key), elList);
  }

  private String cleanKey(final String str) {
    final String[] keyArr = str.split("\\.");
    return keyArr[keyArr.length - 1];
  }

  public List<Map<String, String>> getDictionary() {
    Map<String, String> map;
    final Set<String> keyList = this.elMap.keySet();
    for (int i = 0; i < this.elCnt; i++) {
      map = new HashMap<String, String>();
      for (final String key : keyList) {
        map.put(key, this.elMap.get(key).get(i).trim());
      }
      this.objectList.add(map);
    }
    return this.objectList;
  }
}
