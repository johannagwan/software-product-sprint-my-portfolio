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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if ((int) request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<Event> updatedEvents = addUpdatedEvents(events, request);
    // If ALL the events are for non-meeting attendees, just return list of whole day.
    if (updatedEvents.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<TimeRange> slotsAvailable = findSlotsAvailable(updatedEvents, request);

    return slotsAvailable;
  }

  private List<Event> addUpdatedEvents(Collection<Event> events, MeetingRequest request) {
    List<Event> updatedEvents = new ArrayList<>();
    List<Event> eventsList = new ArrayList<>(events);

    Collections.sort(eventsList, new Comparator<Event>() {
      @Override
      public int compare(Event a, Event b) {
        return Long.compare(a.getWhen().start(), b.getWhen().start());
      }
    });

    // Add earliest VALID event to updatedEvents first, to use as a comparison for subsequent event.
    int firstEventNum = 0;
    boolean findingFirstEvent = true;
    Collection<String> meetingAttendees = request.getAttendees();
    while (findingFirstEvent && firstEventNum < events.size()) {
      Event event = eventsList.get(firstEventNum);
      Collection<String> eventAttendees = event.getAttendees();

      // If every event attendees is NOT a meeting attendee, ignore the event.
      if (!meetingAttendees.containsAll(eventAttendees)) {
        firstEventNum++;
        continue;
      }

      firstEventNum++;
      updatedEvents.add(event);
      findingFirstEvent = false;
    }

    // Combine events if they overlap or contain each other so that we don't need to double count overlapping events.
    int updatedEventsIdx = 0;
    for (int i = firstEventNum; i < events.size(); i++) {
      Event event = eventsList.get(i);
      Collection<String> eventAttendees = event.getAttendees();

      // If every event attendees is NOT a meeting attendee, ignore the event.
      if (!meetingAttendees.containsAll(eventAttendees)) {
        continue;
      }

      // Check for overlaps.
      Event prevEvent = updatedEvents.get(updatedEventsIdx);
      if (event.getWhen().overlaps(prevEvent.getWhen())) { // If overlaps, replace the current prevEvent, take the later ending time.
        int laterTiming = 0;
        if (prevEvent.getWhen().end() > event.getWhen().end()) {
          laterTiming = prevEvent.getWhen().end();
        } else {
          laterTiming = event.getWhen().end();
        }
        
        TimeRange newTimeRange = TimeRange.fromStartEnd(prevEvent.getWhen().start(), laterTiming, false);
        updatedEvents.set(updatedEventsIdx, new Event("", newTimeRange, Collections.emptySet()));
      } else {
        updatedEvents.add(event);
        updatedEventsIdx++;
      }      
    }

    return updatedEvents;
  }

  private Collection<TimeRange> findSlotsAvailable(List<Event> updatedEvents, MeetingRequest request) {
    Collection<TimeRange> slotsAvailable = new ArrayList<>();
    int eventIdx = 0;
    Event event = updatedEvents.get(eventIdx);
    int currStartingTime = TimeRange.START_OF_DAY;
    int currEndingTime = event.getWhen().start();

    // Add timerange to available slots if the duration of the timerange is longer than the meeting duration.
    while (currStartingTime < TimeRange.END_OF_DAY && currEndingTime <= TimeRange.END_OF_DAY) {
      int dur = currEndingTime - currStartingTime;
      if (dur >= (int) request.getDuration()) {
        boolean inclusive = false;
        if (currEndingTime == TimeRange.END_OF_DAY) {
          inclusive = true;
        }
        TimeRange timeRange = TimeRange.fromStartEnd(currStartingTime, currEndingTime, inclusive);
        slotsAvailable.add(timeRange);
      }

      currStartingTime = updatedEvents.get(eventIdx).getWhen().end();
      if (currEndingTime == TimeRange.END_OF_DAY) {
        break;
      } else if (eventIdx == updatedEvents.size() - 1) {
        currEndingTime = TimeRange.END_OF_DAY;
      } else {
        eventIdx++;
        currEndingTime = updatedEvents.get(eventIdx).getWhen().start();
      }
    }

    return slotsAvailable;
  }
}
