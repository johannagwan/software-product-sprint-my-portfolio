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

document.addEventListener("DOMContentLoaded", function(){
  getComments();
});


/**
 * Adds a random greeting to the page.
 */
function addRandomFact() {
  const greetings =
      ['I sleep 12 hours long on weekends and still take a 4-hour nap sometimes', 
      'I have no official surname', 
      'I have telegram sticker for every situation', 
      'I am very boring'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('quote-container');
  greetingContainer.innerText = greeting;
}

/**
 * Fetches stats from the servers and adds them to the DOM.
 */
function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    // comments is an object, not a string, so we have to
    // reference its fields to create HTML content
    const commentsListElement = document.getElementById('comments-container');
    commentsListElement.innerHTML = '';

    comments.map((comment) => {
      commentsListElement.appendChild(createListElement(comment));
      //commentsListElement.appendChild(createCommentSection(comment));
    });
  });
}

/** Creates an <li> element containing text. */
function createListElement(comment) {
  const liElement = document.createElement('li');
  liElement.innerText = "USERNAME: " + comment.username + " COMMENT: " + comment.commentBody;
  return liElement;
}

function createCommentSection(comment) {
  const commentWrapper = document.createElement("div");
  commentWrapper.id = "comment-wrapper";

  commentWrapper.innerHTML = '<div id="comment-header"> <p id="username-display"></p>\
    <p id="timestamp-display"></p></div> <p id="comment-display"></p> <hr class="solid" />';
  
  const commentHeader = document.getElementById("comment-header");

  const usernameDisplay = document.getElementById("username-display");
  usernameDisplay.innerHTML = comment.username;
  
  const timestampDisplay = document.getElementById("timestamp-display");
  timestampDisplay.innerHTML = comment.timestamp;

  const commentDisplay = document.getElementById("comment-display");
  commentDisplay.innerHTML = comment.commentBody;

  const divider = document.createElement("hr");

  // Append username and timestamp to comment header
  commentHeader.appendChild(usernameDisplay);
  commentHeader.appendChild(timestampDisplay);

  // Append child nodes to comment wrapper
  commentWrapper.appendChild(commentHeader);
  commentWrapper.appendChild(commentDisplay);
  commentWrapper.append(divide);

  return commentWrapper;
}
