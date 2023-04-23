package tech.skagedal.hahabit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.skagedal.hahabit.testing.HahabitTest;

// This is an integration test mostly because I want to learn about configuration,
// should more reasonably be just a simple unit test.
@HahabitTest
class HabitSummarizerTest {
  private final HabitSummarizer summarizer;

  HabitSummarizerTest(@Autowired HabitSummarizer summarizer) {
    this.summarizer = summarizer;
  }

  @Test
  void test_days() {
    Assertions.assertEquals(
        "You have done clean 100 days in a row!",
        summarizer.summarize("clean")
    );
  }
}