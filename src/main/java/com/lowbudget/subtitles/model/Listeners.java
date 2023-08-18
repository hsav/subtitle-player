/*
 * MIT License
 *
 * Copyright (c) 2023 Charalampos Savvidis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lowbudget.subtitles.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Listeners<T> {

    private final List<T> list = new ArrayList<>();

    public void add(T listener) {
        this.list.add(listener);
    }

    public void remove(T listener) {
        list.remove(listener);
    }

    public void clear() {
        this.list.clear();
    }

    public void fireEvent(Consumer<T> action) {
        // iterate the list from the end so listeners can remove themselves during this loop
        for (int i = list.size() - 1; i >= 0; i--) {
            T listener = list.get(i);
            action.accept(listener);
        }
    }
}
