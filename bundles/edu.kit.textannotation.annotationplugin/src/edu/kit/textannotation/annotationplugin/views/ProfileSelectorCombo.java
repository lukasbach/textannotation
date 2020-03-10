package edu.kit.textannotation.annotationplugin.views;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.utils.ComboSelectionListener;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.util.List;
import java.util.function.Consumer;

public class ProfileSelectorCombo {
    public final Combo combo;

    public ProfileSelectorCombo(
            Composite parent,
            List<AnnotationProfile> profiles,
            Consumer<AnnotationProfile> onSelect,
            @Nullable String defaultId
    ) {
        combo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
        profiles.forEach(p -> {
            // Add numbers at the end of the values in case multiple profiles with the same names exist
            int i = 0;
            for (String val: combo.getItems()) {
                if (val.startsWith(p.getName())) {
                    i++;
                }
            }

            if (i > 0) {
                combo.add(p.getName() + " " + i);
            } else {
                combo.add(p.getName());
            }
        });
        combo.select(profiles.indexOf(new AnnotationProfile(defaultId, "")));
        ComboSelectionListener.create(combo, (e) -> {
            onSelect.accept(profiles.get(e.index));
        });
    }
}
