package net.picker;

import android.content.Context;
import android.text.TextUtils;

import net.others.SharedPreferenceUtility;
import net.simplifiedcoding.carrypark.R;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Country {
    public static Country[] COUNTRIES = new Country[0];
    public static Country[] COUNTRIESLang = new Country[0];
    private String code;
    private String name;
    private String dialCode;
    private int flag = -1;
    private static List<Country> allCountriesList;
    private Context context;


    public Country(String code, String name, String dialCode, int flag) {
        this.code = code;
        this.name = name;
        this.dialCode = dialCode;
        this.flag = flag;
    }

    public Country() {
    }

    public String getDialCode() {
        return this.dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
        if(TextUtils.isEmpty(this.name)) {
            this.name = (new Locale("", code)).getDisplayName();
        }

    }

    public String getName() {
        return this.name;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void loadFlagByCode(Context context) {
        if(this.flag == -1) {
            try {
                this.flag = context.getResources().getIdentifier("flag_" + this.code.toLowerCase(Locale.ENGLISH), " R.drawable", context.getPackageName());
            } catch (Exception var3) {
                var3.printStackTrace();
                this.flag = -1;
            }

        }
    }

    public static List<Country> getAllCountries() {

        if (SharedPreferenceUtility.isJapanease())
        {
            COUNTRIESLang = new Country[]{ new Country("AE", "アラブ首長国連邦", "+971",  R.drawable.flag_ae), new Country("AF", "アフガニスタン", "+93",  R.drawable.flag_af),  new Country("AI", "アンギラ", "+1",  R.drawable.flag_ai), new Country("AL", "アルバニア", "+355",  R.drawable.flag_al), new Country("AM", "アルメニア", "+374",  R.drawable.flag_am), new Country("AO", "アンゴラ", "+244",  R.drawable.flag_ao), new Country("AQ", "南極大陸", "+672",  R.drawable.flag_aq), new Country("AR", "アルゼンチン", "+54",  R.drawable.flag_ar) , new Country("AT", "オーストリア", "+43",  R.drawable.flag_at), new Country("AU", "オーストラリア", "+61",  R.drawable.flag_au),   new Country("BD", "バングラデシュ", "+880",  R.drawable.flag_bd), new Country("BE", "ベルギー", "+32",  R.drawable.flag_be),new Country("BG", "ブルガリア", "+359",  R.drawable.flag_bg), new Country("BH", "バーレーン", "+973",  R.drawable.flag_bh),   new Country("BM", "バミューダ", "+1",  R.drawable.flag_bm),new Country("BO", "ボリビア", "+591",  R.drawable.flag_bo), new Country("BR", "ブラジル", "+55",  R.drawable.flag_br), new Country("BZ", "ベリーズ", "+501",  R.drawable.flag_bz), new Country("CA", "カナダ", "+1",  R.drawable.flag_ca),   new Country("CF", "中央アフリカ共和国", "+236",   R.drawable.flag_cf), new Country("CG", "コンゴ", "+242",   R.drawable.flag_cg), new Country("CH", "スイス", "+41",   R.drawable.flag_ch),  new Country("CL", "チリ", "+56",   R.drawable.flag_cl), new Country("CM", "カメルーン", "+237",   R.drawable.flag_cm), new Country("CN", "中国", "+86",   R.drawable.flag_cn), new Country("CO", "コロンビア", "+57",   R.drawable.flag_co), new Country("CR", "コスタリカ", "+506",   R.drawable.flag_cr), new Country("CU", "キューバ", "+53",   R.drawable.flag_cu),new Country("CZ", "チェコ共和国", "+420",  R.drawable.flag_cz), new Country("DE", "ドイツ", "+49",  R.drawable.flag_de), new Country("DK", "デンマーク", "+45",  R.drawable.flag_dk), new Country("DM", "ドミニカ", "+1",  R.drawable.flag_dm), new Country("DZ", "アルジェリア", "+213",  R.drawable.flag_dz), new Country("EC", "エクアドル", "+593",  R.drawable.flag_ec), new Country("EG", "エジプト", "+20",  R.drawable.flag_eg),new Country("ER", "エリトリア", "+291",  R.drawable.flag_er), new Country("ES", "スペイン", "+34",  R.drawable.flag_es), new Country("ET", "エチオピア", "+251",  R.drawable.flag_et), new Country("FI", "フィンランド", "+358",  R.drawable.flag_fi), new Country("FJ", "フィジー", "+679",  R.drawable.flag_fj),  new Country("FR", "フランス", "+33",  R.drawable.flag_fr), new Country("GB", "イギリス", "+44",  R.drawable.flag_gb),  new Country("GL", "グリーンランド", "+299",  R.drawable.flag_gl),  new Country("GR", "ギリシャ", "+30",  R.drawable.flag_gr), new Country("HK", "香港", "+852",  R.drawable.flag_hk),new Country("HU", "ハンガリー", "+36",  R.drawable.flag_hu), new Country("ID", "インドネシア", "+62",  R.drawable.flag_id), new Country("IE", "アイルランド", "+353",  R.drawable.flag_ie), new Country("IL", "イスラエル", "+972",  R.drawable.flag_il), new Country("IN", "インド", "+91",  R.drawable.flag_in), new Country("IQ", "イラク", "+964",  R.drawable.flag_iq),  new Country("IS", "アイスランド", "+354",  R.drawable.flag_is), new Country("IT", "イタリア", "+39",  R.drawable.flag_it), new Country("JE", "ジャージー", "+44",  R.drawable.flag_je), new Country("JM", "ジャマイカ", "+1",  R.drawable.flag_jm), new Country("JO", "ヨルダン", "+962",  R.drawable.flag_jo), new Country("JP", "日本", "+81",  R.drawable.flag_jp),  new Country("KP", "北朝鮮", "+850",  R.drawable.flag_kp), new Country("KR", "韓国", "+82",  R.drawable.flag_kr), new Country("KW", "クウェート", "+965",  R.drawable.flag_kw), new Country("LB", "レバノン", "+961",  R.drawable.flag_lb), new Country("LK", "スリランカ", "+94",  R.drawable.flag_lk), new Country("ML", "マリ", "+223",  R.drawable.flag_ml), new Country("MM", "ミャンマー", "+95",  R.drawable.flag_mm), new Country("MN", "モンゴル", "+976",  R.drawable.flag_mn), new Country("MT", "マルタ", "+356",  R.drawable.flag_mt),  new Country("MX", "メキシコ", "+52",  R.drawable.flag_mx), new Country("MY", "マレーシア", "+60",  R.drawable.flag_my),     new Country("NL", "オランダ", "+31",  R.drawable.flag_nl), new Country("NO", "ノルウェー", "+47",  R.drawable.flag_no), new Country("NP", "ネパール", "+977",  R.drawable.flag_np),  new Country("NZ", "ニュージーランド", "+64",  R.drawable.flag_nz), new Country("OM", "オマーン", "+968",  R.drawable.flag_om),  new Country("PE", "ペルー", "+51",  R.drawable.flag_pe),  new Country("PH", "フィリピン", "+63",  R.drawable.flag_ph), new Country("PK", "パキスタン", "+92",  R.drawable.flag_pk), new Country("PL", "ポーランド", "+48",  R.drawable.flag_pl), new Country("QA", "カタール", "+974",  R.drawable.flag_qa),  new Country("RS", "セルビア", "+381",  R.drawable.flag_rs), new Country("RU", "ロシア", "+7",  R.drawable.flag_ru),  new Country("SA", "サウジアラビア", "+966",  R.drawable.flag_sa),  new Country("SD", "スーダン", "+249",  R.drawable.flag_sd), new Country("SE", "スウェーデン", "+46",  R.drawable.flag_se), new Country("SG", "シンガポール", "+65",  R.drawable.flag_sg),  new Country("SO", "ソマリア", "+252",  R.drawable.flag_so),     new Country("SZ", "スワジランド", "+268",  R.drawable.flag_sz),  new Country("TH", "タイ", "+66",  R.drawable.flag_th),   new Country("TR", "トルコ", "+90",  R.drawable.flag_tr),  new Country("TW", "台湾", "+886",  R.drawable.flag_tw),  new Country("UA", "ウクライナ", "+380",  R.drawable.flag_ua),  new Country("US", "アメリカ", "+1",  R.drawable.flag_us),  new Country("YE", "イエメン", "+967",  R.drawable.flag_ye),  new Country("ZA", "南アフリカ", "+27",  R.drawable.flag_za),  new Country("ZW", "ジンバブエ", "+263",  R.drawable.flag_zw)};

        }
        else if (SharedPreferenceUtility.isEnglish())
        {
            COUNTRIESLang = new Country[]{ new Country("AE", "United Arab Emirates", "+971",  R.drawable.flag_ae), new Country("AF", "Afghanistan", "+93",  R.drawable.flag_af), new Country("AI", "Anguilla", "+1",  R.drawable.flag_ai), new Country("AL", "Albania", "+355",  R.drawable.flag_al), new Country("AM", "Armenia", "+374",  R.drawable.flag_am), new Country("AO", "Angola", "+244",  R.drawable.flag_ao), new Country("AQ", "Antarctica", "+672",  R.drawable.flag_aq), new Country("AR", "Argentina", "+54",  R.drawable.flag_ar) , new Country("AT", "Austria", "+43",  R.drawable.flag_at), new Country("AU", "Australia", "+61",  R.drawable.flag_au), new Country("BD", "Bangladesh", "+880",  R.drawable.flag_bd), new Country("BE", "Belgium", "+32",  R.drawable.flag_be),new Country("BG", "Bulgaria", "+359",  R.drawable.flag_bg), new Country("BH", "Bahrain", "+973",  R.drawable.flag_bh),   new Country("BM", "Bermuda", "+1",  R.drawable.flag_bm),  new Country("BO", "Bolivia, Plurinational State of", "+591",  R.drawable.flag_bo), new Country("BR", "Brazil", "+55",  R.drawable.flag_br), new Country("BZ", "Belize", "+501",  R.drawable.flag_bz), new Country("CA", "Canada", "+1",  R.drawable.flag_ca),new Country("CF", "Central African Republic", "+236",  R.drawable.flag_cf), new Country("CG", "Congo", "+242",   R.drawable.flag_cg), new Country("CH", "Switzerland", "+41",   R.drawable.flag_ch),  new Country("CL", "Chile", "+56",   R.drawable.flag_cl), new Country("CM", "Cameroon", "+237",   R.drawable.flag_cm), new Country("CN", "China", "+86",   R.drawable.flag_cn), new Country("CO", "Colombia", "+57",   R.drawable.flag_co), new Country("CR", "Costa Rica", "+506",   R.drawable.flag_cr), new Country("CU", "Cuba", "+53",   R.drawable.flag_cu),  new Country("CZ", "Czech Republic", "+420",   R.drawable.flag_cz), new Country("DE", "Germany", "+49",   R.drawable.flag_de),new Country("DK", "Denmark", "+45",   R.drawable.flag_dk), new Country("DM", "Dominica", "+1",   R.drawable.flag_dm), new Country("DZ", "Algeria", "+213",   R.drawable.flag_dz), new Country("EC", "Ecuador", "+593",   R.drawable.flag_ec), new Country("EE", "Estonia", "+372",   R.drawable.flag_ee), new Country("EG", "Egypt", "+20",   R.drawable.flag_eg), new Country("ER", "Eritrea", "+291",  R.drawable.flag_er), new Country("ES", "Spain", "+34",  R.drawable.flag_es), new Country("ET", "Ethiopia", "+251",  R.drawable.flag_et), new Country("FI", "Finland", "+358",  R.drawable.flag_fi), new Country("FJ", "Fiji", "+679",  R.drawable.flag_fj),  new Country("FR", "France", "+33",  R.drawable.flag_fr),new Country("GB", "United Kingdom", "+44",  R.drawable.flag_gb),   new Country("GL", "Greenland", "+299",  R.drawable.flag_gl),new Country("GR", "Greece", "+30",  R.drawable.flag_gr),  new Country("HK", "Hong Kong", "+852",  R.drawable.flag_hk),  new Country("HU", "Hungary", "+36",  R.drawable.flag_hu), new Country("ID", "Indonesia", "+62",  R.drawable.flag_id), new Country("IE", "Ireland", "+353",  R.drawable.flag_ie), new Country("IL", "Israel", "+972",  R.drawable.flag_il), new Country("IN", "India", "+91",  R.drawable.flag_in), new Country("IQ", "Iraq", "+964",  R.drawable.flag_iq), new Country("IS", "Iceland", "+354",  R.drawable.flag_is), new Country("IT", "Italy", "+39",  R.drawable.flag_it), new Country("JE", "Jersey", "+44",  R.drawable.flag_je), new Country("JM", "Jamaica", "+1",  R.drawable.flag_jm), new Country("JO", "Jordan", "+962",  R.drawable.flag_jo), new Country("JP", "Japan", "+81",  R.drawable.flag_jp),   new Country("KP", "North Korea", "+850",  R.drawable.flag_kp), new Country("KR", "South Korea", "+82",  R.drawable.flag_kr), new Country("KW", "Kuwait", "+965",  R.drawable.flag_kw),new Country("LB", "Lebanon", "+961",  R.drawable.flag_lb),   new Country("LK", "Sri Lanka", "+94",  R.drawable.flag_lk),  new Country("ML", "Mali", "+223",  R.drawable.flag_ml), new Country("MM", "Myanmar", "+95",  R.drawable.flag_mm), new Country("MN", "Mongolia", "+976",  R.drawable.flag_mn), new Country("MT", "Malta", "+356",  R.drawable.flag_mt),new Country("MX", "Mexico", "+52",  R.drawable.flag_mx), new Country("MY", "Malaysia", "+60",  R.drawable.flag_my),   new Country("NL", "Netherlands", "+31",  R.drawable.flag_nl), new Country("NO", "Norway", "+47",  R.drawable.flag_no), new Country("NP", "Nepal", "+977",  R.drawable.flag_np),  new Country("NZ", "New Zealand", "+64",  R.drawable.flag_nz), new Country("OM", "Oman", "+968",  R.drawable.flag_om),  new Country("PE", "Peru", "+51",  R.drawable.flag_pe),   new Country("PH", "Philippines", "+63",  R.drawable.flag_ph), new Country("PK", "Pakistan", "+92",  R.drawable.flag_pk), new Country("PL", "Poland", "+48",  R.drawable.flag_pl), new Country("QA", "Qatar", "+974",  R.drawable.flag_qa), new Country("RS", "Serbia", "+381",  R.drawable.flag_rs), new Country("RU", "Russia", "+7",  R.drawable.flag_ru),  new Country("SA", "Saudi Arabia", "+966",  R.drawable.flag_sa),   new Country("SD", "Sudan", "+249",  R.drawable.flag_sd), new Country("SE", "Sweden", "+46",  R.drawable.flag_se), new Country("SG", "Singapore", "+65",  R.drawable.flag_sg),   new Country("SO", "Somalia", "+252",  R.drawable.flag_so),  new Country("SZ", "Swaziland", "+268",  R.drawable.flag_sz),  new Country("TH", "Thailand", "+66",  R.drawable.flag_th), new Country("TR", "Turkey", "+90",  R.drawable.flag_tr),  new Country("TW", "Taiwan", "+886",  R.drawable.flag_tw),new Country("UA", "Ukraine", "+380",  R.drawable.flag_ua), new Country("US", "United States", "+1",  R.drawable.flag_us), new Country("YE", "Yemen", "+967",  R.drawable.flag_ye),  new Country("ZA", "South Africa", "+27",  R.drawable.flag_za),new Country("ZW", "Zimbabwe", "+263",  R.drawable.flag_zw)};

        }
        else if (SharedPreferenceUtility.isKorean())
        {
            COUNTRIESLang = new Country[]{ new Country("AE", "아랍 에미리트", "+971",  R.drawable.flag_ae), new Country("AF", "아프가니스탄", "+93",  R.drawable.flag_af), new Country("AI", "앵 귈라", "+1",  R.drawable.flag_ai), new Country("AL", "알바니아", "+355",  R.drawable.flag_al), new Country("AM", "아르메니아", "+374",  R.drawable.flag_am), new Country("AO", "앙골라", "+244",  R.drawable.flag_ao), new Country("AQ", "남극", "+672",  R.drawable.flag_aq), new Country("AR", "아르헨티나", "+54",  R.drawable.flag_ar), new Country("AT", "오스트리아", "+43",  R.drawable.flag_at), new Country("AU", "호주", "+61",  R.drawable.flag_au),  new Country("BD", "방글라데시", "+880",  R.drawable.flag_bd), new Country("BE", "벨기에", "+32",  R.drawable.flag_be), new Country("BG", "불가리아", "+359",  R.drawable.flag_bg), new Country("BH", "바레인", "+973",  R.drawable.flag_bh),  new Country("BM", "버뮤다", "+1",  R.drawable.flag_bm),  new Country("BO", "볼리비아", "+591",  R.drawable.flag_bo), new Country("BR", "브라질", "+55",  R.drawable.flag_br),  new Country("BZ", "벨리즈", "+501",  R.drawable.flag_bz), new Country("CA", "캐나다", "+1",  R.drawable.flag_ca), new Country("CF", "중앙아프리카 공화국", "+236",  R.drawable.flag_cf), new Country("CG", "콩고", "+242",   R.drawable.flag_cg), new Country("CH", "스위스", "+41",   R.drawable.flag_ch),  new Country("CL", "칠레", "+56",   R.drawable.flag_cl), new Country("CM", "카메룬", "+237",   R.drawable.flag_cm), new Country("CN", "중국", "+86",   R.drawable.flag_cn), new Country("CO", "콜롬비아", "+57",   R.drawable.flag_co), new Country("CR", "코스타리카", "+506",   R.drawable.flag_cr), new Country("CU", "쿠바", "+53",   R.drawable.flag_cu),  new Country("CZ", "체코 공화국", "+420",  R.drawable.flag_cz), new Country("DE", "독일", "+49",   R.drawable.flag_de),  new Country("DK", "덴마크", "+45",   R.drawable.flag_dk), new Country("DM", "도미니카", "+1",   R.drawable.flag_dm),new Country("DZ", "알제리", "+213",   R.drawable.flag_dz), new Country("EC", "에콰도르", "+593",   R.drawable.flag_ec), new Country("EE", "에스토니아", "+372",   R.drawable.flag_ee), new Country("EG", "이집트", "+20",   R.drawable.flag_eg),new Country("ER", "에리트레아", "+291",   R.drawable.flag_er), new Country("ES", "스페인", "+34",   R.drawable.flag_es), new Country("ET", "에티오피아", "+251",   R.drawable.flag_et), new Country("FI", "핀란드", "+358",   R.drawable.flag_fi), new Country("FJ", "피지", "+679",   R.drawable.flag_fj), new Country("FR", "프랑스", "+33",   R.drawable.flag_fr),new Country("GB", "영국", "+44",   R.drawable.flag_gb),  new Country("GL", "그린란드", "+299",   R.drawable.flag_gl), new Country("GR", "그리스", "+30",   R.drawable.flag_gr),  new Country("HK", "홍콩", "+852",   R.drawable.flag_hk),new Country("HU", "헝가리", "+36",   R.drawable.flag_hu), new Country("ID", "인도네시아", "+62",   R.drawable.flag_id), new Country("IE", "아일랜드", "+353",   R.drawable.flag_ie), new Country("IL", "아일랜드", "+972",   R.drawable.flag_il),   new Country("IN", "인도", "+91",   R.drawable.flag_in),  new Country("IQ", "이라크", "+964",   R.drawable.flag_iq),  new Country("IS", "아이슬란드", "+354",   R.drawable.flag_is), new Country("IT", "이탈리아", "+39",   R.drawable.flag_it), new Country("JE", "저지", "+44",  R.drawable.flag_je), new Country("JM", "자메이카", "+1",   R.drawable.flag_jm), new Country("JO", "요르단", "+962",   R.drawable.flag_jo), new Country("JP", "일본", "+81",  R.drawable.flag_jp), new Country("KP", "북한", "+850",  R.drawable.flag_kp), new Country("KR", "대한민국", "+82",  R.drawable.flag_kr), new Country("KW", "쿠웨이트", "+965",  R.drawable.flag_kw),   new Country("LB", "레바논", "+961",  R.drawable.flag_lb),  new Country("LK", "스리랑카", "+94",  R.drawable.flag_lk), new Country("ML", "말리", "+223",  R.drawable.flag_ml), new Country("MM", "미얀마", "+95",  R.drawable.flag_mm), new Country("MN", "몽골리아", "+976",  R.drawable.flag_mn),  new Country("MT", "몰타", "+356",  R.drawable.flag_mt), new Country("MX", "멕시코", "+52",  R.drawable.flag_mx), new Country("MY", "말레이시아", "+60",  R.drawable.flag_my),  new Country("NL", "네덜란드", "+31",  R.drawable.flag_nl), new Country("NO", "노르웨이", "+47",  R.drawable.flag_no), new Country("NP", "네팔", "+977",  R.drawable.flag_np), new Country("NZ", "뉴질랜드", "+64",  R.drawable.flag_nz), new Country("OM", "오만", "+968",  R.drawable.flag_om), new Country("PE", "페루", "+51",  R.drawable.flag_pe), new Country("PH", "필리핀 제도", "+63",  R.drawable.flag_ph), new Country("PK", "파키스탄", "+92",  R.drawable.flag_pk), new Country("PL", "폴란드", "+48",  R.drawable.flag_pl), new Country("QA", "카타르", "+974",  R.drawable.flag_qa), new Country("RS", "세르비아", "+381",  R.drawable.flag_rs), new Country("RU", "러시아", "+7",  R.drawable.flag_ru), new Country("SA", "사우디 아라비아", "+966",  R.drawable.flag_sa), new Country("SD", "수단", "+249",  R.drawable.flag_sd), new Country("SE", "스웨덴", "+46",  R.drawable.flag_se), new Country("SG", "싱가포르", "+65",  R.drawable.flag_sg), new Country("SO", "소말리아", "+252",  R.drawable.flag_so),   new Country("SZ", "스와질랜드", "+268",  R.drawable.flag_sz),  new Country("TH", "태국", "+66",  R.drawable.flag_th),  new Country("TR", "대만", "+90",  R.drawable.flag_tr), new Country("UA", "우크라이나", "+380",  R.drawable.flag_ua),  new Country("US", "미국", "+1",  R.drawable.flag_us),  new Country("YE", "예멘", "+967",  R.drawable.flag_ye),  new Country("ZA", "남아프리카", "+27",  R.drawable.flag_za), new Country("ZW", "짐바브웨", "+263",  R.drawable.flag_zw)};

        }
        else if (SharedPreferenceUtility.isChinease())
        {
            COUNTRIESLang = new Country[]{ new Country("AE", "阿拉伯联合酋长国", "+971",  R.drawable.flag_ae), new Country("AF", "阿富汗", "+93",  R.drawable.flag_af), new Country("AI", "安圭拉岛", "+1",  R.drawable.flag_ai), new Country("AL", "阿尔巴尼亚", "+355",  R.drawable.flag_al), new Country("AM", "亚美尼亚", "+374",  R.drawable.flag_am), new Country("AO", "安哥拉", "+244",  R.drawable.flag_ao), new Country("AQ", "南极洲", "+672",  R.drawable.flag_aq), new Country("AR", "阿根廷", "+54",  R.drawable.flag_ar), new Country("AT", "奥地利", "+43",  R.drawable.flag_at), new Country("AU", "澳大利亚", "+61",  R.drawable.flag_au),   new Country("BD", "孟加拉国", "+880",  R.drawable.flag_bd), new Country("BE", "比利时", "+32",  R.drawable.flag_be),  new Country("BG", "保加利亚", "+359",  R.drawable.flag_bg), new Country("BH", "巴林", "+973",  R.drawable.flag_bh),   new Country("BM", "百慕大", "+1",  R.drawable.flag_bm), new Country("BO", "玻利维亚", "+591",  R.drawable.flag_bo),  new Country("BR", "巴西", "+55",  R.drawable.flag_br), new Country("BZ", "伯利茲", "+501",  R.drawable.flag_bz), new Country("CA", "加拿大", "+1",  R.drawable.flag_ca), new Country("CF", "中非共和國", "+236",  R.drawable.flag_cf), new Country("CG", "刚果", "+242",   R.drawable.flag_cg), new Country("CH", "瑞士", "+41",   R.drawable.flag_ch),  new Country("CL", "智利", "+56",   R.drawable.flag_cl), new Country("CM", "喀麦隆", "+237",   R.drawable.flag_cm), new Country("CN", "中国", "+86",   R.drawable.flag_cn), new Country("CO", "哥伦比亚", "+57",   R.drawable.flag_co), new Country("CR", "哥斯达黎加", "+506",   R.drawable.flag_cr), new Country("CU", "古巴", "+53",   R.drawable.flag_cu), new Country("CZ", "捷克共和国", "+420",   R.drawable.flag_cz), new Country("DE", "德国", "+49",   R.drawable.flag_de),  new Country("DK", "丹麦", "+45",   R.drawable.flag_dk), new Country("DM", "多米尼加", "+1",   R.drawable.flag_dm),  new Country("DZ", "阿尔及利亚", "+213",   R.drawable.flag_dz), new Country("EC", "厄瓜多尔", "+593",   R.drawable.flag_ec), new Country("EE", "愛沙尼亞", "+372",   R.drawable.flag_ee), new Country("EG", "埃及", "+20",   R.drawable.flag_eg),  new Country("ER", "厄立特里亚", "+291",   R.drawable.flag_er), new Country("ES", "西班牙", "+34",   R.drawable.flag_es), new Country("ET", "埃塞俄比亚", "+251",  R.drawable.flag_et), new Country("FI", "芬兰", "+358",  R.drawable.flag_fi), new Country("FJ", "斐济", "+679",  R.drawable.flag_fj), new Country("FR", "法国", "+33",  R.drawable.flag_fr),  new Country("GB", "英国", "+44",  R.drawable.flag_gb), new Country("GL", "格陵兰", "+299",  R.drawable.flag_gl),  new Country("GR", "希腊", "+30",  R.drawable.flag_gr),  new Country("HK", "香港", "+852",  R.drawable.flag_hk),  new Country("HU", "匈牙利", "+36",  R.drawable.flag_hu), new Country("ID", "印度尼西亚", "+62",  R.drawable.flag_id), new Country("IE", "爱尔兰", "+353",  R.drawable.flag_ie), new Country("IL", "爱尔兰", "+972",  R.drawable.flag_il), new Country("IN", "印度", "+91",  R.drawable.flag_in),  new Country("IQ", "伊拉克", "+964",  R.drawable.flag_iq),  new Country("IS", "冰岛", "+354",  R.drawable.flag_is), new Country("IT", "意大利", "+39",  R.drawable.flag_it), new Country("JE", "球衣", "+44",  R.drawable.flag_je), new Country("JM", "的牙买加", "+1",  R.drawable.flag_jm), new Country("JO", "约旦", "+962",  R.drawable.flag_jo), new Country("JP", "日本", "+81",  R.drawable.flag_jp),  new Country("KP", "北朝鲜", "+850",  R.drawable.flag_kp), new Country("KR", "南韩", "+82",  R.drawable.flag_kr), new Country("KW", "科威特", "+965",  R.drawable.flag_kw),   new Country("LB", "黎巴嫩的", "+961",  R.drawable.flag_lb),  new Country("LK", "斯里兰卡", "+94",  R.drawable.flag_lk),    new Country("ML", "马里", "+223",  R.drawable.flag_ml), new Country("MM", "缅甸", "+95",  R.drawable.flag_mm), new Country("MN", "蒙古", "+976",  R.drawable.flag_mn),   new Country("MT", "马耳他", "+356",  R.drawable.flag_mt),  new Country("MX", "墨西哥", "+52",  R.drawable.flag_mx), new Country("MY", "马来西亚", "+60",  R.drawable.flag_my),new Country("NL", "荷兰", "+31",  R.drawable.flag_nl), new Country("NO", "挪威", "+47",  R.drawable.flag_no), new Country("NP", "尼泊尔", "+977",  R.drawable.flag_np),  new Country("NZ", "新西兰", "+64",  R.drawable.flag_nz), new Country("OM", "阿曼", "+968",  R.drawable.flag_om),  new Country("PE", "秘鲁", "+51",  R.drawable.flag_pe),new Country("PH", "菲律宾", "+63",  R.drawable.flag_ph), new Country("PK", "巴基斯坦", "+92",  R.drawable.flag_pk), new Country("PL", "波兰", "+48",  R.drawable.flag_pl),  new Country("QA", "卡塔尔", "+974",  R.drawable.flag_qa), new Country("RS", "塞尔维亚", "+381",  R.drawable.flag_rs), new Country("RU", "俄罗斯", "+7",  R.drawable.flag_ru),  new Country("SA"," 沙特阿拉伯", "+966",  R.drawable.flag_sa),  new Country("SD", "苏丹", "+249",  R.drawable.flag_sd), new Country("SE", "瑞典", "+46",  R.drawable.flag_se), new Country("SG", "新加坡", "+65",  R.drawable.flag_sg),  new Country("SO", "索马里", "+252",  R.drawable.flag_so),   new Country("SZ", "斯威士兰", "+268",  R.drawable.flag_sz),  new Country("TH", "泰国", "+66",  R.drawable.flag_th),   new Country("TN", "泰國", "+216",  R.drawable.flag_tn),  new Country("TR", "土耳其", "+90",  R.drawable.flag_tr),  new Country("TW", "台湾", "+886",  R.drawable.flag_tw),  new Country("UA", "乌克兰", "+380",  R.drawable.flag_ua), new Country("US", "美国", "+1",  R.drawable.flag_us), new Country("YE", "也门", "+967",  R.drawable.flag_ye),  new Country("ZA", "南非", "+27",  R.drawable.flag_za),  new Country("ZW", "津巴布韦", "+263",  R.drawable.flag_zw)};

        }

            allCountriesList = Arrays.asList(COUNTRIESLang);


        return allCountriesList;
    }

    public static Country getCountryByISO(String countryIsoCode) {
        countryIsoCode = countryIsoCode.toUpperCase();
        Country c = new Country();
        c.setCode(countryIsoCode);
        int i = Arrays.binarySearch(COUNTRIES, c, new Country.ISOCodeComparator());
        return i < 0?null:COUNTRIES[i];
    }

    public static Country getCountryByName(String countryName) {
        Country[] var1 = COUNTRIES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Country c = var1[var3];
            if(countryName.equals(c.getName())) {
                return c;
            }
        }

        return null;
    }

    public static Country getCountryByLocale(Locale locale) {
        String countryIsoCode = locale.getISO3Country().substring(0, 2).toLowerCase();
        return getCountryByISO(countryIsoCode);
    }



    public static class NameComparator implements Comparator<Country> {
        public NameComparator() {
        }

        public int compare(Country country, Country t1) {
            return country.name.compareTo(t1.name);
        }
    }

    public static class ISOCodeComparator implements Comparator<Country> {
        public ISOCodeComparator() {
        }

        public int compare(Country country, Country t1) {
            return country.code.compareTo(t1.code);
        }
    }
}