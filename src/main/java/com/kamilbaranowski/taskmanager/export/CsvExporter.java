package com.kamilbaranowski.taskmanager.export;
import com.kamilbaranowski.taskmanager.task.model.Task;
import java.util.List;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

public class CsvExporter {

    public static String getCsv(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("id,title,description,status,visibility,createdAt,createdBy,modifiedAt,modifiedBy,dueDate,parentId,taskCode\n");
        for (Task task : tasks) {
            sb.append(task.getId()).append(",")
                    .append(escapeCsv(task.getTitle())).append(",")
                    .append(escapeCsv(task.getDescription())).append(",")
                    .append(task.getStatus()).append(",")
                    .append(task.getVisibility()).append(",")
                    .append(task.getCreatedAt()).append(",")
                    .append(task.getCreatedBy()).append(",")
                    .append(task.getModifiedAt()).append(",")
                    .append(task.getModifiedBy()).append(",")
                    .append(task.getDueDate()).append(",")
                    .append(task.getParentId()).append(",")
                    .append(task.getTaskCode()).append("\n");
        }
        return sb.toString();
    }
}
