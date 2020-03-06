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

/**
 * This UI component defines a reusable title component, which offers the following subcomponents:
 *
 * <ul>
 *     <li>A primary header text</li>
 *     <li>A subheader text</li>
 *     <li>A list of buttons which is displayed to the right of the header texts</li>
 * </ul>
 */
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

    /**
     * Create a new header.
     * @param text the text of the primary header title
     * @return the Header instance
     */
    public static Header withTitle(String text) {
        Header h = new Header();
        h.title = text;
        return h;
    }

    /**
     * Extend an existing header by a subtitle
     * @param text the text of the subheader title
     * @return the Header instance for chainability
     */
    public Header withSubTitle(String text) {
        this.subtitle = text;
        return this;
    }

    /**
     * Extend an existing header by a button
     * @param text the displayed text of the button
     * @param onClick the handler which is invoked when the button is clicked
     * @return the Header instance for chainability
     */
    public Header withButton(String text, Runnable onClick) {
        buttons.add(new HeaderButton(text, onClick));
        return this;
    }

    /**
     * Render the header component into the supplied composite
     * @param parent the composite in which the header is rendered into
     * @return the composite which contains solely the header component
     */
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
            Label subtitleLabel = new Label(left, SWT.WRAP);
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
