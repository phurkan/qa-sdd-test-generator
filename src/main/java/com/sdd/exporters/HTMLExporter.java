package com.sdd.exporters;

import com.sdd.core.TestCaseModel;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HTMLExporter - Generates styled HTML Traceability Matrix report.
 */
public class HTMLExporter {

    public static void export(List<TestCaseModel> tcs, String path, String ticketId) throws Exception {
        long ui = tcs.stream().filter(t -> t.getType().equals("UI")).count();
        long api = tcs.stream().filter(t -> t.getType().equals("API")).count();
        long a11y = tcs.stream().filter(t -> t.getType().equals("ACCESSIBILITY")).count();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>");
            pw.println("<title>SDD Traceability - " + ticketId + "</title>");
            pw.println("<style>");
            pw.println("*{box-sizing:border-box;margin:0;padding:0}body{font-family:'Segoe UI',sans-serif;background:#f4f6f9;color:#222}");
            pw.println(".hdr{background:linear-gradient(135deg,#00696f,#1a5676);color:#fff;padding:28px 40px}");
            pw.println(".hdr h1{font-size:22px;margin-bottom:6px}.hdr p{opacity:.85;font-size:13px}");
            pw.println(".stats{display:flex;gap:14px;padding:20px 40px;background:#fff;border-bottom:1px solid #e0e0e0;flex-wrap:wrap}");
            pw.println(".stat{background:#f0f8f9;border-radius:8px;padding:14px 22px;text-align:center;min-width:110px;border-top:3px solid #00696f}");
            pw.println(".stat .n{font-size:26px;font-weight:700;color:#00696f}.stat .l{font-size:11px;color:#666;margin-top:3px}");
            pw.println(".body{padding:24px 40px}");
            pw.println(".sec-title{font-size:16px;font-weight:600;color:#00696f;margin:28px 0 10px;padding-left:10px;border-left:4px solid #00696f}");
            pw.println("table{width:100%;border-collapse:collapse;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.06);margin-bottom:24px}");
            pw.println("th{background:#00696f;color:#fff;padding:11px 12px;text-align:left;font-size:12px;white-space:nowrap}");
            pw.println("td{padding:9px 12px;font-size:12px;border-bottom:1px solid #eee;vertical-align:top}");
            pw.println("tr:nth-child(even) td{background:#fafafa}");
            pw.println(".badge{display:inline-block;padding:2px 9px;border-radius:20px;font-size:11px;font-weight:600}");
            pw.println(".UI{background:#e0f2f1;color:#00695c}.API{background:#e3f2fd;color:#1565c0}.ACCESSIBILITY{background:#ede7f6;color:#4527a0}");
            pw.println(".Critical{background:#ffebee;color:#c62828}.High{background:#fff3e0;color:#e65100}");
            pw.println(".Medium{background:#e8f5e9;color:#2e7d32}.Low{background:#f3e5f5;color:#6a1b9a}");
            pw.println(".Pending{background:#fff9c4;color:#f57f17}.Pass{background:#e8f5e9;color:#1b5e20}");
            pw.println(".Fail{background:#ffebee;color:#b71c1c}.Blocked{background:#eceff1;color:#455a64}");
            pw.println("</style></head><body>");

            pw.println("<div class='hdr'><h1>SDD Framework — Requirement Traceability Matrix</h1>");
            pw.println("<p>Ticket: <strong>" + ticketId + "</strong> &nbsp;|&nbsp; Generated: " + now +
                " &nbsp;|&nbsp; Total: " + tcs.size() + " test cases</p></div>");

            pw.println("<div class='stats'>");
            pw.println(stat(String.valueOf(tcs.size()), "Total TCs"));
            pw.println(stat(String.valueOf(ui), "UI Tests"));
            pw.println(stat(String.valueOf(api), "API Tests"));
            pw.println(stat(String.valueOf(a11y), "Accessibility"));
            pw.println(stat(String.valueOf(tcs.stream().filter(t->t.getPriority().equals("Critical")).count()), "Critical"));
            pw.println(stat(String.valueOf(tcs.stream().filter(t->t.getPriority().equals("High")).count()), "High"));
            pw.println("</div>");

            pw.println("<div class='body'>");
            for (String type : new String[]{"UI", "API", "ACCESSIBILITY"}) {
                List<TestCaseModel> filtered = tcs.stream().filter(t -> t.getType().equals(type)).toList();
                if (filtered.isEmpty()) continue;
                String label = switch (type) {
                    case "UI"            -> "UI / Frontend Test Cases";
                    case "API"           -> "API / Backend Test Cases";
                    case "ACCESSIBILITY" -> "Accessibility / WCAG Test Cases";
                    default              -> type;
                };
                pw.println("<div class='sec-title'>" + label + " (" + filtered.size() + ")</div>");
                pw.println("<table><tr><th>TC ID</th><th>Req ID</th><th>Title</th><th>Type</th>" +
                    "<th>Priority</th><th>Module</th><th>Expected Result</th><th>Status</th></tr>");
                for (TestCaseModel tc : filtered) {
                    pw.println("<tr><td><strong>" + tc.getTcId() + "</strong></td>");
                    pw.println("<td>" + tc.getRequirementId() + "</td>");
                    pw.println("<td>" + tc.getTitle() + "</td>");
                    pw.println("<td><span class='badge " + tc.getType() + "'>" + tc.getType() + "</span></td>");
                    pw.println("<td><span class='badge " + tc.getPriority() + "'>" + tc.getPriority() + "</span></td>");
                    pw.println("<td>" + tc.getModule() + "</td>");
                    pw.println("<td>" + tc.getExpectedResult().replace("\n", "<br>") + "</td>");
                    pw.println("<td><span class='badge " + tc.getStatus() + "'>" + tc.getStatus() + "</span></td></tr>");
                }
                pw.println("</table>");
            }
            pw.println("</div></body></html>");
        }
        System.out.println("HTML report exported: " + path);
    }

    private static String stat(String num, String label) {
        return "<div class='stat'><div class='n'>" + num + "</div><div class='l'>" + label + "</div></div>";
    }
}
