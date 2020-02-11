package edu.kit.textannotation.annotationplugin.views;

import edu.kit.textannotation.annotationplugin.utils.LayoutUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.List;

public class Header {
    private LayoutUtilities lu = new LayoutUtilities();
    private String title;
    private String subtitle;
    private List<HeaderButton> buttons;

    private class HeaderButton {
        String text;
        Runnable onClick;

        HeaderButton(String text, Runnable onClick) {
            this.text = text;
            this.onClick = onClick;
        }
    }

    private Header() {
        title = null;
        subtitle = null;
        buttons = new ArrayList<>(4);
    }

    public static Header withTitle(String text) {
        Header h = new Header();
        h.title = text;
        return h;
    }

    public Header withSubTitle(String text) {
        this.subtitle = text;
        return this;
    }

    public Header withButton(String text, Runnable onClick) {
        buttons.add(new HeaderButton(text, onClick));
        return this;
    }

    public Composite render(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayoutData(lu.horizontalFillingGridData());
        container.setLayout(lu.gridLayout().withNumCols(2).get());

        Composite left = new Composite(container, SWT.NULL);
        left.setLayoutData(lu.horizontalFillingGridData());
        left.setLayout(lu.fillLayout().withVertical().get());

        Composite right = new Composite(container, SWT.NULL);
        right.setLayoutData(lu.horizontalFillingGridData());
        right.setLayout(lu.fillLayout().withHorizontal().get());

        Label titleLabel = new Label(left, SWT.NULL);
        titleLabel.setText(title);
        setFontSize(titleLabel, 14);

        if (subtitle != null) {
            Label subtitleLabel = new Label(left, SWT.NULL);
            subtitleLabel.setText(subtitle);
            setFontSize(subtitleLabel, 10);
        }

        buttons.forEach(b -> {
            Button button = new Button(right, SWT.PUSH);
            button.setText(b.text);
            button.addListener(SWT.Selection, e -> b.onClick.run());
        });

        container.layout();

        return container;
    }

    private void setFontSize(Label label, int size) {
        FontData[] fD = label.getFont().getFontData();
        fD[0].setHeight(size);
        label.setFont(new Font(Display.getDefault(), fD[0]));
    }
}
