package com.app.service.impl;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.app.entity.LearningMaterial;
import com.app.repository.LearningMaterialRepository;
import com.app.service.LearningMaterialService;

@Service
public class LearningMaterialServiceImpl implements LearningMaterialService {

    private final LearningMaterialRepository learningMaterialRepository;

    public LearningMaterialServiceImpl(LearningMaterialRepository learningMaterialRepository) {
        this.learningMaterialRepository = learningMaterialRepository;
    }

    @Override
    public LearningMaterial saveLearningMaterial(LearningMaterial lm) {
        if (lm == null || lm.getContent() == null) {
            return learningMaterialRepository.save(lm);
        }

        String sanitized = sanitizeAllowedStyles(lm.getContent());
        // Final clean with a safelist that allows basic tags + images and allows style attribute on common text tags
        Safelist safelist = Safelist.basicWithImages();
        safelist.addTags("span", "div", "table", "thead", "tbody", "tfoot", "tr", "td", "th");
        safelist.addAttributes("span", "style");
        safelist.addAttributes("div", "style");
        safelist.addAttributes("p", "style");
        safelist.addAttributes("h1", "style");
        safelist.addAttributes("h2", "style");
        safelist.addAttributes("h3", "style");
        safelist.addAttributes("td", "style");
        safelist.addAttributes("th", "style");

        String safeHtml = Jsoup.clean(sanitized, safelist);
        lm.setContent(safeHtml);
        return learningMaterialRepository.save(lm);
    }

    // Sanitize style attributes to keep only allowed CSS properties and safe values
    private String sanitizeAllowedStyles(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Elements styled = doc.select("[style]");
        Set<String> allowedProps = Set.of("color", "background-color", "font-size");

        for (Element el : styled) {
            String style = el.attr("style");
            if (style == null || style.isBlank()) {
                el.removeAttr("style");
                continue;
            }

            String[] decls = style.split(";");
            Map<String, String> kept = new LinkedHashMap<>();
            for (String d : decls) {
                String part = d.trim();
                if (part.isEmpty()) continue;
                int idx = part.indexOf(':');
                if (idx <= 0) continue;
                String prop = part.substring(0, idx).trim().toLowerCase();
                String value = part.substring(idx + 1).trim();
                if (!allowedProps.contains(prop)) continue;

                // Validate value based on property
                if (prop.equals("color") || prop.equals("background-color")) {
                    if (isValidColor(value)) {
                        kept.put(prop, value);
                    }
                } else if (prop.equals("font-size")) {
                    if (isValidFontSize(value)) {
                        kept.put(prop, value);
                    }
                }
            }

            if (kept.isEmpty()) {
                el.removeAttr("style");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> e : kept.entrySet()) {
                    if (sb.length() > 0) sb.append("; ");
                    sb.append(e.getKey()).append(": ").append(e.getValue());
                }
                el.attr("style", sb.toString());
            }
        }

        return doc.body().html();
    }

    // Accept hex colors (#fff, #ffffff), rgb(), rgba(), or basic color names (letters)
    private boolean isValidColor(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase();
        // hex
        if (s.matches("^#([a-f0-9]{3}|[a-f0-9]{6})$")) return true;
        // rgb() or rgba()
        if (s.matches("^rgb\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*\\)$")) return true;
        if (s.matches("^rgba\\(\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*\\d{1,3}\\s*,\\s*(0|0?\\.\\d+|1)\\s*\\)$")) return true;
        // basic color names (letters only, e.g., red, blue)
        if (s.matches("^[a-z]+$")) return true;
        return false;
    }

    // Accept font sizes like 12px, 1.2em, 100%, etc. Limit numeric part to reasonable range
    private boolean isValidFontSize(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase();
        // px, em, rem, %, pt
        if (s.matches("^\\d{1,3}px$")) return true; // up to 999px
        if (s.matches("^[0-9]+(\\.[0-9]+)?(em|rem)$")) return true;
        if (s.matches("^\\d{1,3}%$")) return true; // up to 999%
        if (s.matches("^\\d{1,3}pt$")) return true;
        return false;
    }

    @Override
    public List<LearningMaterial> getAllLearningMaterials() {
        return learningMaterialRepository.findAll();
    }

    @Override
    public List<LearningMaterial> getLearningMaterialsBySubject(String subjectId) {
        return learningMaterialRepository.findBySubjectSubjectCode(subjectId);
    }

    @Override
    public List<LearningMaterial> getLearningMaterialsByInstructor(Long instructorId) {
        return learningMaterialRepository.findByInstructorId(instructorId);
    }
}
