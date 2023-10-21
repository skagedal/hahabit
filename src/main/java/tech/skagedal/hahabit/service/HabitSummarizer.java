package tech.skagedal.hahabit.service;

import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
public class HabitSummarizer {
  private final SummaryConfiguration configuration;

  public HabitSummarizer(SummaryConfiguration configuration) {
    this.configuration = configuration;
  }

  public String summarize(String habit) {
    return "You have done " + habit + " " + configuration.days() + " days in a row!";
  }

  @SuppressWarnings("java:S106")
  public static void main(String[] args) {
    System.out.println(DataSize.ofMegabytes(1).toBytes());
  }
}
