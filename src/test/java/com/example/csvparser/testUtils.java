package com.example.csvparser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class testUtils {

}

@Getter
@Setter
@NoArgsConstructor
class csvSettings{
    private int numValid = 5;
    private int numDup = 2;
    private int numInvalid = 2;
    private int numBlank = 1;
    private boolean hasHeader = true;
}