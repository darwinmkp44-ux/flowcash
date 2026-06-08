---
name: FlowCash
colors:
  surface: '#faf9fe'
  surface-dim: '#dad9df'
  surface-bright: '#faf9fe'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f3f8'
  surface-container: '#eeedf3'
  surface-container-high: '#e9e7ed'
  surface-container-highest: '#e3e2e7'
  on-surface: '#1a1b1f'
  on-surface-variant: '#414755'
  inverse-surface: '#2f3034'
  inverse-on-surface: '#f1f0f5'
  outline: '#717786'
  outline-variant: '#c1c6d7'
  surface-tint: '#005bc1'
  primary: '#0058bc'
  on-primary: '#ffffff'
  primary-container: '#0070eb'
  on-primary-container: '#fefcff'
  inverse-primary: '#adc6ff'
  secondary: '#006e28'
  on-secondary: '#ffffff'
  secondary-container: '#6ffb85'
  on-secondary-container: '#00732a'
  tertiary: '#4c4aca'
  on-tertiary: '#ffffff'
  tertiary-container: '#6664e4'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e2ff'
  primary-fixed-dim: '#adc6ff'
  on-primary-fixed: '#001a41'
  on-primary-fixed-variant: '#004493'
  secondary-fixed: '#72fe88'
  secondary-fixed-dim: '#53e16f'
  on-secondary-fixed: '#002107'
  on-secondary-fixed-variant: '#00531c'
  tertiary-fixed: '#e2dfff'
  tertiary-fixed-dim: '#c2c1ff'
  on-tertiary-fixed: '#0c006a'
  on-tertiary-fixed-variant: '#3631b4'
  background: '#faf9fe'
  on-background: '#1a1b1f'
  surface-variant: '#e3e2e7'
typography:
  display:
    fontFamily: Inter
    fontSize: 34px
    fontWeight: '700'
    lineHeight: 41px
    letterSpacing: -0.4px
  headline:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.4px
  headline-mobile:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
    letterSpacing: -0.4px
  title-1:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.4px
  title-2:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 25px
    letterSpacing: -0.4px
  body:
    fontFamily: Inter
    fontSize: 17px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: -0.4px
  body-emphasized:
    fontFamily: Inter
    fontSize: 17px
    fontWeight: '600'
    lineHeight: 22px
    letterSpacing: -0.4px
  callout:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 21px
    letterSpacing: -0.3px
  subheadline:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: -0.2px
  footnote:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: -0.1px
  caption:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-page: 16px
  gutter-grid: 12px
  stack-sm: 4px
  stack-md: 8px
  stack-lg: 16px
  stack-xl: 24px
  section-gap: 32px
---

## Brand & Style

The brand personality is defined by clarity, efficiency, and reliability. This design system adopts a **Modern iOS-inspired Minimalism**, focusing on high legibility and a sense of "digital lightness." It is designed to feel like a first-party utility—integrated, unobtrusive, and exceptionally polished.

The target audience consists of Brazilian users seeking financial autonomy without cognitive overhead. The UI should evoke a sense of calm and control through ample white space, a strictly organized hierarchy, and a refined "Glassmorphism" influence used sparingly for navigation elements. The aesthetic prioritizes function through form, utilizing the familiarity of standard iOS patterns to reduce learning curves.

## Colors

The palette is rooted in the native iOS color language to ensure immediate accessibility and familiarity.

*   **Primary (System Blue):** Used for primary actions, links, and active states. It signals "Action."
*   **Secondary (System Green):** Specifically reserved for positive financial trends, income, and "Success" states.
*   **Neutral & Backgrounds:** The background is pure white (`#FFFFFF`). Surfaces and grouped list backgrounds use a subtle gray (`#F2F2F7`).
*   **System Colors:** Use `#FF3B30` (System Red) for expenses, debts, or destructive actions.

Maintain high contrast for all text elements against white backgrounds to meet WCAG AA standards.

## Typography

While SF Pro is the native target, **Inter** is utilized in this design system to provide a systematic, clean, and highly legible alternative that mirrors the functional geometric qualities of iOS typography.

- **Scale:** Follows the Apple Dynamic Type scale.
- **Language:** Localization for Portuguese (PT-BR) requires checking for longer word strings in buttons and labels (e.g., "Transferência" vs "Transfer").
- **Currency:** Financial values should use `body-emphasized` or `title-1` depending on the context, always using the "R$" prefix with a non-breaking space.

## Layout & Spacing

The layout follows a **Fluid Grid** model typical of iOS. Content is primarily contained within a 16px lateral margin.

*   **Grouped Lists:** Use the standard iOS inset-group style for settings and data entry.
*   **Vertical Rhythm:** Use 8px increments for internal component spacing and 16px/24px for spacing between distinct logical blocks.
*   **Safe Areas:** Respect top (Notch/Dynamic Island) and bottom (Home Indicator) safe areas strictly.

## Elevation & Depth

This design system uses **Tonal Layers** and **Ambient Shadows** to create a sense of organized stackable depth without visual clutter.

1.  **Level 0 (Base):** System Background (`#F2F2F7`).
2.  **Level 1 (Cards/Cells):** Pure White (`#FFFFFF`). These feature a very soft, diffused shadow: `0px 2px 8px rgba(0, 0, 0, 0.04)`.
3.  **Level 2 (Modals/Popovers):** Elevated White. Shadow: `0px 4px 16px rgba(0, 0, 0, 0.08)`.

Separators are 0.5pt (1px) lines with `#C6C6C8` color, used only when necessary to distinguish items within the same surface level.

## Shapes

The shape language is strictly governed by the **Rounded** (Apple-style) aesthetic.

*   **Standard Containers:** 10px to 12px corner radius.
*   **Large Buttons:** 12px corner radius or fully pill-shaped for specific call-to-actions.
*   **Input Fields:** 10px corner radius.
*   **Smooth Corners:** Where possible, use continuous corner curvature (squircle) rather than standard geometric fillets to align with the iOS hardware and software language.

## Components

### Buttons
- **Primary:** Background Primary Color, White text, 12px radius, Semi-bold.
- **Secondary:** Light gray background or ghost style with Primary text.
- **Tertiary/Plain:** Text-only, primary color, typically used in Navigation Bars (e.g., "Editar", "Ver Tudo").

### Cards
- White background, 12px radius, subtle 4% opacity shadow.
- Padding: 16px internal. Used for account balances and transaction summaries.

### Input Fields
- Modern iOS style: Either a simple bottom border in grouped lists or a soft gray filled background for search bars. Labels should use the `footnote` style above the field.

### Chips / Badges
- Used for categories (e.g., "Alimentação", "Lazer").
- Small 2px-4px radius or fully rounded. Low-saturation background tints of the category color with high-saturation text.

### Lists
- Standard "Chevron" Disclosure Indicators for navigable items.
- Left-aligned icons with a 28x28px background container (rounded 6px).

### Financial Indicators
- **Positive Values:** Green, prefixed with "+".
- **Negative Values:** Black (Standard) or Red (Critical), prefixed with "-".
