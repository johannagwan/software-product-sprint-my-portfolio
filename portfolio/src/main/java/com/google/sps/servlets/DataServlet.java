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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.google.gson.Gson;
import com.google.sps.data.Comment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final String COMMENT = "Comment";
  private final String USERNAME = "username";
  private final String COMMENT_BODY = "commentBody";
  private final String TIMESTAMP = "timestamp";
  private final String LANGUAGE_CODE = "languageCode";
  private Translate translate = TranslateOptions.getDefaultInstance().getService();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create Query for Comment Datastore
    Query query = new Query(COMMENT).addSort(TIMESTAMP, SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    String languageCode = request.getParameter(LANGUAGE_CODE);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String username = (String) entity.getProperty(USERNAME);
      String commentBody = (String) entity.getProperty(COMMENT_BODY); 
      String timestamp = (String) entity.getProperty(TIMESTAMP);

      // Do the translation for comment body.
      Translation commentBodyTranslation =
        translate.translate(commentBody, Translate.TranslateOption.targetLanguage(languageCode));
      String commentBodyTranslatedText = commentBodyTranslation.getTranslatedText();

      // Do the translation for timestamp.
      Translation timestampTranslation =
        translate.translate(timestamp, Translate.TranslateOption.targetLanguage(languageCode));
      String timestampTranslatedText = timestampTranslation.getTranslatedText();

      Comment comment = new Comment(username, commentBodyTranslatedText, timestampTranslatedText);
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form
    String username = request.getParameter("user-name").trim();
    String commentBody = request.getParameter("text-input").trim(); 
    String languageCode = request.getParameter(LANGUAGE_CODE);
    String DEFAULT_LANG = "en";
    if (languageCode == null) {
      languageCode = DEFAULT_LANG;
    }
    
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy hh:mm z");  
    String timestamp = dateFormat.format(date).toString();
    
    // Entity for the comment.
    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(USERNAME, username);
    commentEntity.setProperty(COMMENT_BODY, commentBody);
    commentEntity.setProperty(TIMESTAMP, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);	    

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html?languageCode=" + languageCode);
  }
}
