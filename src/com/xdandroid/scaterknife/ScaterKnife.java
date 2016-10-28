package com.xdandroid.scaterknife;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;

import java.util.*;
import java.util.stream.*;

/**
 * Created by XingDa on 2016/10/27.
 */
public class ScaterKnife extends AnAction {

    private static boolean hasText(CharSequence cs) {
        return cs != null && !cs.toString().trim().equals("");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Document doc = editor.getDocument();
        SelectionModel sm = editor.getSelectionModel();
        int start = sm.getSelectionStart();
        int end = sm.getSelectionEnd();
        String itemViewName = doc.getText(new TextRange(start, end));
        boolean shouldAddItemViewName = hasText(itemViewName) && itemViewName.length() <= 50;
        String fullText = doc.getText();
        int firstAnnoPos = fullText.indexOf("@BindView");
        boolean hasExcludeFirstBlank = false;
        Scanner scanner = new Scanner(fullText);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int index = line.indexOf("@BindView");
            if (index <= -1) continue;
            if (!hasExcludeFirstBlank) {
                hasExcludeFirstBlank = true;
                firstAnnoPos = firstAnnoPos - index;
            }
            String[] splitArray = line.split(" ");
            StringBuilder lineSb = new StringBuilder();
            for (int i = 0; i < index; i++) lineSb.append(" ");
            List<String> partList = Arrays
                    .stream(splitArray)
                    .filter(ScaterKnife::hasText)
                    .map(s -> {
                        int partNo = 1;
                        if (s.contains("@BindView")) partNo = 0;
                        if (s.contains(";")) partNo = 2;
                        switch (partNo) {
                            case 0:
                                int left = s.indexOf("(");
                                int right = s.indexOf(")");
                                return s.substring(left + 1, right);
                            case 1:
                                return s;
                            case 2:
                                return s.substring(0, s.length() - 1);
                        }
                        throw new AssertionError();
                    }).collect(Collectors.toList());
            lineSb.append("lazy val ")
                  .append(partList.get(2))
                  .append(" = ");
            if (shouldAddItemViewName) {
                lineSb.append(itemViewName)
                      .append(".");
            }
            lineSb.append("findViewById(")
                  .append(partList.get(0))
                  .append(").asInstanceOf[")
                  .append(partList.get(1))
                  .append("]\n");
            sb.append(lineSb);
        }
        sb.append("\n");
        int finalFirstAnnoPos = firstAnnoPos;
        WriteCommandAction.runWriteCommandAction(project, () -> doc.insertString(Math.max(finalFirstAnnoPos, 0), sb.toString()));
        scanner.close();
    }
}
