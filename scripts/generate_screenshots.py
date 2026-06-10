#!/usr/bin/env python3
"""Generate mockup screenshots of FlowCash app screens."""

from PIL import Image, ImageDraw, ImageFont
import os

WIDTH, HEIGHT = 1080, 2340  # Pixel 8 resolution
PRIMARY_BLUE = (0, 102, 204)
GREEN = (21, 128, 61)
RED = (186, 26, 26)
BG = (250, 249, 254)
SURFACE = (255, 255, 255)
CARD_BG = (255, 255, 255)
TEXT_PRIMARY = (26, 27, 31)
TEXT_SECONDARY = (65, 71, 85)
CHART_BG = (227, 226, 231)
FILTER_BG = (238, 237, 243)
DARK_GRAY = (100, 100, 100)
LIGHT_GRAY = (200, 200, 200)


def rounded_rect(draw, xy, r, fill=None, outline=None, width=1):
    x1, y1, x2, y2 = xy
    draw.rounded_rectangle(xy, radius=r, fill=fill, outline=outline, width=width)


def draw_status_bar(draw):
    draw.rectangle([0, 0, WIDTH, 48], fill=(255, 255, 255))


def draw_bottom_nav_pro(draw, y_start=2240):
    draw.rectangle([0, y_start, WIDTH, 2340], fill=(255, 255, 255))
    draw.line([0, y_start, WIDTH, y_start], fill=(235, 235, 235), width=1)

    items = [
        ("Home", 0), ("Hist\u00f3rico", 1), ("+", 2),
        ("Estat\u00edsticas", 3), ("Perfil", 4)
    ]
    spacing = WIDTH // 5
    for i, (label, idx) in enumerate(items):
        cx = spacing * i + spacing // 2
        if idx == 2:
            # Center ADD button - bigger circle
            draw.ellipse([cx - 32, y_start - 10, cx + 32, y_start + 54],
                         fill=PRIMARY_BLUE)
            draw.text((cx, y_start + 22), "+", fill=(255, 255, 255),
                      font=get_font(32), anchor="mm")
        else:
            dot_y = y_start + 12
            icon_color = PRIMARY_BLUE if idx == 0 else LIGHT_GRAY
            draw.ellipse([cx - 3, dot_y - 3, cx + 3, dot_y + 3], fill=icon_color)
            text_color = (0, 102, 204) if idx == 0 else DARK_GRAY
            draw.text((cx, y_start + 34), label, fill=text_color,
                      font=get_font(9), anchor="mm")


def draw_bottom_nav_business(draw, y_start=2240):
    draw.rectangle([0, y_start, WIDTH, 2340], fill=(255, 255, 255))
    draw.line([0, y_start, WIDTH, y_start], fill=(235, 235, 235), width=1)

    items = [
        ("Pessoal", 0), ("Business", 1), ("Resumo", 2), ("Defini\u00e7\u00f5es", 3)
    ]
    spacing = (WIDTH - 80) // 4
    for i, (label, idx) in enumerate(items):
        cx = spacing * i + spacing // 2 + 4
        dot_y = y_start + 12
        icon_color = PRIMARY_BLUE if idx == 0 else LIGHT_GRAY
        draw.ellipse([cx - 3, dot_y - 3, cx + 3, dot_y + 3], fill=icon_color)
        text_color = (0, 102, 204) if idx == 0 else DARK_GRAY
        draw.text((cx, y_start + 34), label, fill=text_color,
                  font=get_font(9), anchor="mm")

    # ADD button on the right
    cx = WIDTH - 44
    draw.ellipse([cx - 32, y_start - 10, cx + 32, y_start + 54],
                 fill=PRIMARY_BLUE)
    draw.text((cx, y_start + 22), "+", fill=(255, 255, 255),
              font=get_font(32), anchor="mm")


def get_font(size):
    try:
        return ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", size)
    except (IOError, OSError):
        return ImageFont.load_default()


def generate_pro_home():
    img = Image.new("RGB", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    # Top greeting
    draw.text((30, 70), "Bom dia,", fill=TEXT_SECONDARY, font=get_font(14))
    draw.text((30, 90), "FlowCash User", fill=TEXT_PRIMARY, font=get_font(22))

    # Balance card
    rounded_rect(draw, [20, 130, WIDTH - 20, 380], 16, fill=SURFACE)
    draw.text((35, 150), "SALDO TOTAL", fill=TEXT_SECONDARY, font=get_font(11))
    draw.text((35, 170), "MZN  1,234,567.89", fill=TEXT_PRIMARY, font=get_font(26))

    # Trend
    rounded_rect(draw, [35, 210, 160, 235], 8, fill=(230, 245, 230))
    draw.text((45, 217), "+5.2% este m\u00eas", fill=GREEN, font=get_font(10))

    # Income / Economy boxes
    rounded_rect(draw, [35, 260, 170, 310], 10, fill=FILTER_BG)
    draw.text((50, 268), "RENDIMENTOS", fill=TEXT_SECONDARY, font=get_font(9))
    draw.text((50, 285), "+12.5%", fill=TEXT_PRIMARY, font=get_font(16))

    rounded_rect(draw, [190, 260, WIDTH - 35, 310], 10, fill=FILTER_BG)
    draw.text((205, 268), "ECONOMIA", fill=TEXT_SECONDARY, font=get_font(9))
    draw.text((205, 285), "18%", fill=TEXT_PRIMARY, font=get_font(16))

    # Filter row
    rounded_rect(draw, [20, 400, WIDTH - 20, 440], 12, fill=FILTER_BG)
    filters = ["Tudo", "7d", "15d", "30d"]
    fw = (WIDTH - 56) // 4
    for i, f in enumerate(filters):
        fx = 28 + i * (fw + 8)
        if i == 0:
            rounded_rect(draw, [fx, 405, fx + fw, 435], 8, fill=PRIMARY_BLUE)
            draw.text((fx + fw // 2, 420), f, fill=(255, 255, 255), font=get_font(12), anchor="mm")
        else:
            draw.text((fx + fw // 2, 420), f, fill=TEXT_SECONDARY, font=get_font(12), anchor="mm")

    # Transaction count
    draw.text((30, 465), "128 transa\u00e7\u00f5es", fill=TEXT_PRIMARY, font=get_font(16))

    # Transactions grouped by date
    dates = ["10 de Junho de 2026", "9 de Junho de 2026"]
    tx_data = [
        [("Sal\u00e1rio", "Sal\u00e1rio", "RECEITA", "75,000.00"),
         ("Energia", "Utilidades", "DESPESA", "1,200.00"),
         ("Supermercado", "Compras", "DESPESA", "3,500.00")],
        [("Freelance", "Freelance", "RECEITA", "12,000.00"),
         ("Transporte", "Transporte", "DESPESA", "800.00")]
    ]

    y = 495
    for di, date in enumerate(dates):
        draw.text((30, y), date, fill=TEXT_SECONDARY, font=get_font(11))
        y += 24
        for title, cat, tx_type, amount in tx_data[di]:
            ch = 52
            rounded_rect(draw, [20, y, WIDTH - 20, y + ch], 12, fill=SURFACE)
            icon_color = GREEN if tx_type == "RECEITA" else RED
            draw.ellipse([36, y + 10, 60, y + 34], fill=icon_color)
            draw.text((70, y + 10), title, fill=TEXT_PRIMARY, font=get_font(13))
            draw.text((70, y + 30), cat, fill=TEXT_SECONDARY, font=get_font(10))

            amt_color = GREEN if tx_type == "RECEITA" else TEXT_PRIMARY
            prefix = "+ " if tx_type == "RECEITA" else "- "
            draw.text((WIDTH - 40, y + 14), prefix + amount + " MT",
                      fill=amt_color, font=get_font(12), anchor="rm")
            y += ch + 8

    draw_bottom_nav_pro(draw)
    img.save("screenshots/pro_home.png")
    print("Generated screenshots/pro_home.png")


def generate_pro_resumo():
    img = Image.new("RGB", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    # Header
    draw.text((30, 70), "Resumo", fill=TEXT_PRIMARY, font=get_font(24))

    # Filter row
    rounded_rect(draw, [20, 110, WIDTH - 20, 150], 12, fill=FILTER_BG)
    filters = ["Tudo", "7d", "15d", "30d"]
    fw = (WIDTH - 56) // 4
    for i, f in enumerate(filters):
        fx = 28 + i * (fw + 8)
        if i == 0:
            rounded_rect(draw, [fx, 115, fx + fw, 145], 8, fill=PRIMARY_BLUE)
            draw.text((fx + fw // 2, 130), f, fill=(255, 255, 255), font=get_font(12), anchor="mm")
        else:
            draw.text((fx + fw // 2, 130), f, fill=TEXT_SECONDARY, font=get_font(12), anchor="mm")

    # 3 metric cards
    card_w = (WIDTH - 56) // 3
    cards = [("Saldo", "12.3k", PRIMARY_BLUE), ("Entradas", "45.6k", GREEN), ("Gastos", "33.3k", RED)]
    for i, (label, val, color) in enumerate(cards):
        cx = 24 + i * (card_w + 8)
        rounded_rect(draw, [cx, 170, cx + card_w, 260], 12, fill=SURFACE)
        draw.ellipse([cx + 10, 180, cx + 34, 204], fill=(color[0], color[1], color[2], 30))
        draw.text((cx + 14, 218), label, fill=TEXT_SECONDARY, font=get_font(10))
        draw.text((cx + 14, 235), val, fill=color, font=get_font(18))

    # Income vs Expense chart
    rounded_rect(draw, [20, 285, WIDTH - 20, 520], 12, fill=SURFACE)
    draw.text((35, 300), "Entradas vs Gastos", fill=TEXT_PRIMARY, font=get_font(14))

    # Bar chart
    chart_x, chart_y = 35, 330
    chart_w, chart_h = WIDTH - 70, 140
    draw.line([chart_x, chart_y + chart_h, chart_x + chart_w, chart_y + chart_h],
              fill=CHART_BG, width=1)

    num_bars = 7
    bar_w = (chart_w - (num_bars - 1) * 6) * 0.65 / 2
    for i in range(num_bars):
        bx = chart_x + i * (bar_w * 2 + 6)
        inc_h = 60 + (i * 12) % 60
        exp_h = 40 + (i * 15) % 50
        draw.rectangle([bx, chart_y + chart_h - inc_h, bx + bar_w, chart_y + chart_h],
                       fill=GREEN)
        draw.rectangle([bx + bar_w + 2, chart_y + chart_h - exp_h,
                        bx + bar_w * 2 + 2, chart_y + chart_h],
                       fill=RED)

    # Legend
    draw.ellipse([35, 480, 43, 488], fill=GREEN)
    draw.text((48, 478), "Entradas", fill=TEXT_SECONDARY, font=get_font(10))
    draw.ellipse([125, 480, 133, 488], fill=RED)
    draw.text((138, 478), "Gastos", fill=TEXT_SECONDARY, font=get_font(10))

    # Pie chart - Gastos por Categoria
    rounded_rect(draw, [20, 540, WIDTH - 20, 900], 12, fill=SURFACE)
    draw.text((35, 555), "Gastos por Categoria", fill=TEXT_PRIMARY, font=get_font(14))

    # Donut chart
    cx_pie, cy_pie = WIDTH // 2, 660
    r_outer, r_inner = 75, 55
    # Draw arcs for categories
    colors = [GREEN, RED, (76, 74, 202), (0, 110, 40)]
    segments = [45, 30, 15, 10]
    start_angle = -90
    # Simplified pie as colored arcs
    for si, (seg, col) in enumerate(zip(segments, colors)):
        end_angle = start_angle + seg
        for deg in range(start_angle, end_angle):
            import math
            rad = math.radians(deg)
            x1 = cx_pie + r_outer * math.cos(rad)
            y1 = cy_pie + r_outer * math.sin(rad)
            x2 = cx_pie + r_inner * math.cos(rad)
            y2 = cy_pie + r_inner * math.sin(rad)
            draw.line([x1, y1, x2, y2], fill=col, width=3)
        start_angle = end_angle

    draw.text((cx_pie, cy_pie - 10), "33.3k", fill=TEXT_PRIMARY, font=get_font(14), anchor="mm")
    draw.text((cx_pie, cy_pie + 10), "TOTAL", fill=TEXT_SECONDARY, font=get_font(9), anchor="mm")

    # Category list with progress
    categories = [
        ("Compras", 45.0, GREEN),
        ("Alimenta\u00e7\u00e3o", 30.0, RED),
        ("Transporte", 15.0, (76, 74, 202)),
        ("Outros", 10.0, (0, 110, 40))
    ]
    ycat = 740
    for cat_name, pct, col in categories:
        draw.text((35, ycat), cat_name, fill=TEXT_PRIMARY, font=get_font(12))
        draw.text((WIDTH - 160, ycat), f"{pct:.1f}%", fill=TEXT_PRIMARY, font=get_font(12))
        ycat += 20
        # Progress bar
        rounded_rect(draw, [35, ycat, WIDTH - 35, ycat + 6], 3, fill=CHART_BG)
        pw = int((WIDTH - 70) * pct / 100)
        rounded_rect(draw, [35, ycat, 35 + pw, ycat + 6], 3, fill=col)
        amt_str = f"{pct * 33300 / 100:,.0f} MT"
        draw.text((WIDTH - 40, ycat - 2), amt_str, fill=TEXT_SECONDARY, font=get_font(9), anchor="rm")
        ycat += 24

    draw_bottom_nav_pro(draw)
    img.save("screenshots/pro_resumo.png")
    print("Generated screenshots/pro_resumo.png")


def generate_business_home():
    img = Image.new("RGB", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    # Profile
    draw.text((30, 70), "Pessoal", fill=TEXT_PRIMARY, font=get_font(24))

    # Sub tabs
    tabs = ["Home", "Hist\u00f3rico", "Metas", "Gr\u00e1ficos"]
    tab_y = 110
    fw = (WIDTH - 56) // 4
    for i, t in enumerate(tabs):
        fx = 28 + i * (fw + 8)
        if i == 0:
            rounded_rect(draw, [fx, tab_y, fx + fw, tab_y + 34], 8, fill=PRIMARY_BLUE)
            draw.text((fx + fw // 2, tab_y + 17), t, fill=(255, 255, 255),
                      font=get_font(11), anchor="mm")
        else:
            rounded_rect(draw, [fx, tab_y, fx + fw, tab_y + 34], 8, fill=FILTER_BG)
            draw.text((fx + fw // 2, tab_y + 17), t, fill=TEXT_SECONDARY,
                      font=get_font(11), anchor="mm")

    # Balance card (same as Pro home)
    rounded_rect(draw, [20, 165, WIDTH - 20, 350], 16, fill=SURFACE)
    draw.text((35, 180), "SALDO TOTAL", fill=TEXT_SECONDARY, font=get_font(11))
    draw.text((35, 200), "MZN  1,234,567.89", fill=TEXT_PRIMARY, font=get_font(24))

    # Filter row
    rounded_rect(draw, [20, 375, WIDTH - 20, 415], 12, fill=FILTER_BG)
    filters = ["Tudo", "7d", "15d", "30d"]
    fw = (WIDTH - 56) // 4
    for i, f in enumerate(filters):
        fx = 28 + i * (fw + 8)
        if i == 0:
            rounded_rect(draw, [fx, 380, fx + fw, 410], 8, fill=PRIMARY_BLUE)
            draw.text((fx + fw // 2, 395), f, fill=(255, 255, 255), font=get_font(12), anchor="mm")
        else:
            draw.text((fx + fw // 2, 395), f, fill=TEXT_SECONDARY, font=get_font(12), anchor="mm")

    # Transaction count
    draw.text((30, 440), "128 transa\u00e7\u00f5es", fill=TEXT_PRIMARY, font=get_font(16))

    # Transactions grouped by date (same as Pro)
    dates = ["10 de Junho de 2026", "9 de Junho de 2026"]
    tx_data = [
        [("Sal\u00e1rio", "Sal\u00e1rio", "RECEITA", "75,000.00"),
         ("Energia", "Utilidades", "DESPESA", "1,200.00"),
         ("Supermercado", "Compras", "DESPESA", "3,500.00")],
        [("Freelance", "Freelance", "RECEITA", "12,000.00"),
         ("Transporte", "Transporte", "DESPESA", "800.00")]
    ]

    y = 470
    for di, date in enumerate(dates):
        draw.text((30, y), date, fill=TEXT_SECONDARY, font=get_font(11))
        y += 24
        for title, cat, tx_type, amount in tx_data[di]:
            ch = 52
            rounded_rect(draw, [20, y, WIDTH - 20, y + ch], 12, fill=SURFACE)
            icon_color = GREEN if tx_type == "RECEITA" else RED
            draw.ellipse([36, y + 10, 60, y + 34], fill=icon_color)
            draw.text((70, y + 10), title, fill=TEXT_PRIMARY, font=get_font(13))
            draw.text((70, y + 30), cat, fill=TEXT_SECONDARY, font=get_font(10))

            amt_color = GREEN if tx_type == "RECEITA" else TEXT_PRIMARY
            prefix = "+ " if tx_type == "RECEITA" else "- "
            draw.text((WIDTH - 40, y + 14), prefix + amount + " MT",
                      fill=amt_color, font=get_font(12), anchor="rm")
            y += ch + 8

    draw_bottom_nav_business(draw)
    img.save("screenshots/business_home.png")
    print("Generated screenshots/business_home.png")


def generate_transaction_dialog():
    img = Image.new("RGB", (WIDTH, HEIGHT), (240, 240, 245))
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    # Semi-transparent overlay
    overlay = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 100))
    img.paste(overlay, (0, 0), overlay)

    # Dialog card
    dialog_w, dialog_h = 600, 380
    dx = (WIDTH - dialog_w) // 2
    dy = (HEIGHT - dialog_h) // 2
    rounded_rect(draw, [dx, dy, dx + dialog_w, dy + dialog_h], 24, fill=SURFACE)

    draw.text((WIDTH // 2, dy + 35), "Nova Transa\u00e7\u00e3o",
              fill=TEXT_PRIMARY, font=get_font(18), anchor="mm")
    draw.text((WIDTH // 2, dy + 60), "Selecione o tipo",
              fill=TEXT_SECONDARY, font=get_font(13), anchor="mm")

    # Receita button
    btn_w, btn_h = 500, 56
    bx = (WIDTH - btn_w) // 2
    rounded_rect(draw, [bx, dy + 90, bx + btn_w, dy + 90 + btn_h], 14,
                 fill=(200, 230, 200))
    draw.text((WIDTH // 2, dy + 118), "Receita (Entrada)",
              fill=(0, 80, 30), font=get_font(14), anchor="mm")

    # Gasto button
    rounded_rect(draw, [bx, dy + 160, bx + btn_w, dy + 160 + btn_h], 14,
                 fill=(255, 210, 210))
    draw.text((WIDTH // 2, dy + 188), "Gasto (Despesa)",
              fill=(130, 20, 20), font=get_font(14), anchor="mm")

    # Cancel button
    draw.text((WIDTH // 2, dy + 255), "Cancelar",
              fill=TEXT_SECONDARY, font=get_font(13), anchor="mm")

    img.save("screenshots/transaction_dialog.png")
    print("Generated screenshots/transaction_dialog.png")


def generate_transaction_dialog_despesa():
    img = Image.new("RGB", (WIDTH, HEIGHT), (240, 240, 245))
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    overlay = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 100))
    img.paste(overlay, (0, 0), overlay)

    dialog_w, dialog_h = 600, 420
    dx = (WIDTH - dialog_w) // 2
    dy = (HEIGHT - dialog_h) // 2
    rounded_rect(draw, [dx, dy, dx + dialog_w, dy + dialog_h], 24, fill=SURFACE)

    draw.text((WIDTH // 2, dy + 35), "Novo Gasto",
              fill=TEXT_PRIMARY, font=get_font(18), anchor="mm")

    # Nome field
    rounded_rect(draw, [dx + 40, dy + 75, dx + dialog_w - 40, dy + 120], 12,
                 fill=FILTER_BG)
    draw.text((dx + 55, dy + 90), "Energia", fill=TEXT_PRIMARY, font=get_font(14))

    # Valor field
    rounded_rect(draw, [dx + 40, dy + 145, dx + dialog_w - 40, dy + 190], 12,
                 fill=FILTER_BG)
    draw.text((dx + 55, dy + 160), "MZN  500", fill=PRIMARY_BLUE, font=get_font(14))

    # Salvar button
    rounded_rect(draw, [dx + 40, dy + 225, dx + dialog_w - 40, dy + 277], 14,
                 fill=PRIMARY_BLUE)
    draw.text((WIDTH // 2, dy + 251), "Salvar",
              fill=(255, 255, 255), font=get_font(15), anchor="mm")

    # Voltar button
    draw.text((WIDTH // 2, dy + 300), "Voltar",
              fill=TEXT_SECONDARY, font=get_font(13), anchor="mm")

    img.save("screenshots/transaction_dialog_despesa.png")
    print("Generated screenshots/transaction_dialog_despesa.png")


def generate_business_resumo():
    img = Image.new("RGB", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)
    draw_status_bar(draw)

    draw.text((30, 70), "Resumo", fill=TEXT_PRIMARY, font=get_font(24))

    # Filter row
    rounded_rect(draw, [20, 110, WIDTH - 20, 150], 12, fill=FILTER_BG)
    filters = ["Tudo", "7d", "15d", "30d"]
    fw = (WIDTH - 56) // 4
    for i, f in enumerate(filters):
        fx = 28 + i * (fw + 8)
        if i == 0:
            rounded_rect(draw, [fx, 115, fx + fw, 145], 8, fill=PRIMARY_BLUE)
            draw.text((fx + fw // 2, 130), f, fill=(255, 255, 255), font=get_font(12), anchor="mm")
        else:
            draw.text((fx + fw // 2, 130), f, fill=TEXT_SECONDARY, font=get_font(12), anchor="mm")

    # 3 metric cards
    card_w = (WIDTH - 56) // 3
    cards = [("Saldo", "12.3k", PRIMARY_BLUE), ("Entradas", "45.6k", GREEN), ("Gastos", "33.3k", RED)]
    for i, (label, val, color) in enumerate(cards):
        cx = 24 + i * (card_w + 8)
        rounded_rect(draw, [cx, 170, cx + card_w, 260], 12, fill=SURFACE)
        draw.ellipse([cx + 10, 180, cx + 34, 204], fill=(color[0], color[1], color[2], 30))
        draw.text((cx + 14, 218), label, fill=TEXT_SECONDARY, font=get_font(10))
        draw.text((cx + 14, 235), val, fill=color, font=get_font(18))

    # Bar chart
    rounded_rect(draw, [20, 285, WIDTH - 20, 520], 12, fill=SURFACE)
    draw.text((35, 300), "Entradas vs Gastos", fill=TEXT_PRIMARY, font=get_font(14))

    chart_x, chart_y = 35, 330
    chart_w, chart_h = WIDTH - 70, 140
    draw.line([chart_x, chart_y + chart_h, chart_x + chart_w, chart_y + chart_h], fill=CHART_BG, width=1)

    bar_w = (chart_w - 6 * 6) * 0.65 / 2
    for i in range(7):
        bx = chart_x + i * (bar_w * 2 + 6)
        inc_h = 60 + (i * 12) % 60
        exp_h = 40 + (i * 15) % 50
        draw.rectangle([bx, chart_y + chart_h - inc_h, bx + bar_w, chart_y + chart_h], fill=GREEN)
        draw.rectangle([bx + bar_w + 2, chart_y + chart_h - exp_h, bx + bar_w * 2 + 2, chart_y + chart_h], fill=RED)

    draw.ellipse([35, 480, 43, 488], fill=GREEN)
    draw.text((48, 478), "Entradas", fill=TEXT_SECONDARY, font=get_font(10))
    draw.ellipse([125, 480, 133, 488], fill=RED)
    draw.text((138, 478), "Gastos", fill=TEXT_SECONDARY, font=get_font(10))

    # Pie chart
    rounded_rect(draw, [20, 540, WIDTH - 20, 900], 12, fill=SURFACE)
    draw.text((35, 555), "Gastos por Categoria", fill=TEXT_PRIMARY, font=get_font(14))

    cx_pie, cy_pie = WIDTH // 2, 660
    colors = [GREEN, RED, (76, 74, 202), (0, 110, 40)]
    segments = [45, 30, 15, 10]
    start_angle = -90
    for si, (seg, col) in enumerate(zip(segments, colors)):
        end_angle = start_angle + seg
        for deg in range(start_angle, end_angle):
            import math
            rad = math.radians(deg)
            r_outer, r_inner = 75, 55
            x1 = cx_pie + r_outer * math.cos(rad)
            y1 = cy_pie + r_outer * math.sin(rad)
            x2 = cx_pie + r_inner * math.cos(rad)
            y2 = cy_pie + r_inner * math.sin(rad)
            draw.line([x1, y1, x2, y2], fill=col, width=3)
        start_angle = end_angle

    draw.text((cx_pie, cy_pie - 10), "33.3k", fill=TEXT_PRIMARY, font=get_font(14), anchor="mm")
    draw.text((cx_pie, cy_pie + 10), "TOTAL", fill=TEXT_SECONDARY, font=get_font(9), anchor="mm")

    categories = [
        ("Compras", 45.0, GREEN),
        ("Alimenta\u00e7\u00e3o", 30.0, RED),
        ("Transporte", 15.0, (76, 74, 202)),
        ("Outros", 10.0, (0, 110, 40))
    ]
    ycat = 740
    for cat_name, pct, col in categories:
        draw.text((35, ycat), cat_name, fill=TEXT_PRIMARY, font=get_font(12))
        draw.text((WIDTH - 160, ycat), f"{pct:.1f}%", fill=TEXT_PRIMARY, font=get_font(12))
        ycat += 20
        rounded_rect(draw, [35, ycat, WIDTH - 35, ycat + 6], 3, fill=CHART_BG)
        pw = int((WIDTH - 70) * pct / 100)
        rounded_rect(draw, [35, ycat, 35 + pw, ycat + 6], 3, fill=col)
        amt_str = f"{pct * 33300 / 100:,.0f} MT"
        draw.text((WIDTH - 40, ycat - 2), amt_str, fill=TEXT_SECONDARY, font=get_font(9), anchor="rm")
        ycat += 24

    draw_bottom_nav_business(draw)
    img.save("screenshots/business_resumo.png")
    print("Generated screenshots/business_resumo.png")


if __name__ == "__main__":
    os.makedirs("screenshots", exist_ok=True)
    generate_pro_home()
    generate_pro_resumo()
    generate_business_home()
    generate_business_resumo()
    generate_transaction_dialog()
    generate_transaction_dialog_despesa()
    print(f"\nAll screenshots generated in screenshots/ directory")
    os.system("ls -lh screenshots/")
