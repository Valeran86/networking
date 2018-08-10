package ru.sbt.javaschool.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Message implements Serializable {
    private String source;
    private String target;
    private String text;
    private String command;
}
