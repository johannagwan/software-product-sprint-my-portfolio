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

/**
 * Adds a random greeting to the page.
 */
/*function addRandomFact() {
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

async function getRandomQuoteUsingAsyncAwait() {
  const response = await fetch('/data');
  const responseText = await response.text();
  document.getElementById('quote-container').innerText = responseText;
}*/

/**
 * Fetches stats from the servers and adds them to the DOM.
 */
function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    // stats is an object, not a string, so we have to
    // reference its fields to create HTML content

    const commentsListElement = document.getElementById('quote-container');
    commentsListElement.innerHTML = '';

    comments.forEach((comment) => {
      commentsListElement.appendChild(
        createListElement(comment));
    });
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
