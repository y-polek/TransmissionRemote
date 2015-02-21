/*
 * Copyright 2014 Paul Tsouchlos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.devpaul.filepickerlibrary;

/**
 * Created by Paul Tsouchlos
 */
public interface NameFileDialogInterface {

    /**
     * Called when the name file dialog is dismissed returning the inputted name for the new folder.
     * @param fileName
     */
    public void onReturnFileName(String fileName);
}
