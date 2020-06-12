// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> quotes;

  @Override
  public void init() {
    quotes = new ArrayList<>();
    quotes.add(
        "A ship in port is safe, but that is not what ships are for. "
            + "Sail out to sea and do new things. - Grace Hopper");
    quotes.add("They told me computers could only do arithmetic. - Grace Hopper");
    quotes.add("A ship in port is safe, but that's not what ships are built for. - Grace Hopper");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(convertToJsonUsingGson(quotes));
  }

  private String convertToJsonUsingGson(List list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
