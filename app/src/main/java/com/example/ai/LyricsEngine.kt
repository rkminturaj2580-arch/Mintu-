package com.example.ai

import com.example.model.LyricsLanguage
import com.example.model.LyricsStructure
import com.example.model.SongMood

object LyricsEngine {

    /**
     * Generates a fully structured song with Intro, Verse 1, Pre-Chorus, Chorus, Verse 2, Bridge, Final Chorus, Outro.
     */
    fun generateLyrics(
        title: String,
        themePrompt: String,
        language: LyricsLanguage,
        mood: SongMood
    ): LyricsStructure {
        val songTitle = title.ifBlank { getDefaultTitle(language, mood) }

        return when (language) {
            LyricsLanguage.HINDI -> generateHindiLyrics(songTitle, mood, themePrompt)
            LyricsLanguage.BHOJPURI -> generateBhojpuriLyrics(songTitle, mood, themePrompt)
            LyricsLanguage.PUNJABI -> generatePunjabiLyrics(songTitle, mood, themePrompt)
            LyricsLanguage.TAMIL -> generateTamilLyrics(songTitle, mood, themePrompt)
            LyricsLanguage.BHAKTI -> generateBhaktiLyrics(songTitle, mood, themePrompt)
            LyricsLanguage.ENGLISH -> generateEnglishLyrics(songTitle, mood, themePrompt)
        }
    }

    private fun getDefaultTitle(language: LyricsLanguage, mood: SongMood): String {
        return when (language) {
            LyricsLanguage.HINDI -> if (mood == SongMood.ROMANTIC) "Tere Bina Dil Na Lage" else "Khwabon Ki Dastaan"
            LyricsLanguage.BHOJPURI -> if (mood == SongMood.HAPPY) "Lollypop Lagelu Remix" else "Ae Piya Pardesi"
            LyricsLanguage.PUNJABI -> "Dil Dhadke Desi Beats"
            LyricsLanguage.TAMIL -> "Kanmani Anbodu Kaadhal"
            LyricsLanguage.BHAKTI -> "Hey Shambhu Hey Shiv Shankar"
            LyricsLanguage.ENGLISH -> "Neon Echoes in the Night"
        }
    }

    private fun generateHindiLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return when (mood) {
            SongMood.ROMANTIC -> LyricsStructure(
                title = title,
                language = LyricsLanguage.HINDI,
                mood = mood,
                intro = "(Acoustic guitar strums softly...)\nHmm... hmm...\nKhamoshiyon mein bhi teri aawaz hai,\nHar saans mein tera hi ehsaas hai.",
                verse1 = "Dheere se chhoota hai hawaon ka jhonka,\nJaise tune mera haath thaam liya ho.\nRaahon mein bikhre hain sitaron ke taare,\nMaine tere naam apna jahaan likh diya.",
                preChorus = "Dhadkan meri bas tera naam pukare,\nPal pal yeh lamha bas tujhko sawanre...",
                chorus = "Tere bina dil ab lagta nahi re,\nYeh aashiqui ab mit-ti nahi re.\nTu hai toh subah meri, tu hai toh shaam,\nLikh diya dil pe maine tera hi naam!",
                verse2 = "Chupke se dekha tha pehli baar jab,\nWaqt bhi ruk gaya tha pal do pal tab.\nAnkhon mein teri jo noor hai pyara,\nBan gaya tu hi mera sahara.",
                bridge = "Chahe zamaana jo bhi kahe humse,\nHum toh chalenge sang tere kasam se.\nIshq nahi yeh ibadat ban gaya hai...",
                finalChorus = "Tere bina dil ab lagta nahi re,\nYeh aashiqui ab mit-ti nahi re!\nTu hai toh subah meri, tu hai toh shaam,\nLikh diya dil pe maine tera hi naam!",
                outro = "Haan tera hi naam...\nBas tera hi naam...\n(Music fades with soft bansuri flute...)"
            )
            SongMood.SAD, SongMood.EMOTIONAL -> LyricsStructure(
                title = title,
                language = LyricsLanguage.HINDI,
                mood = mood,
                intro = "(Violin weeps in deep reverb...)\nKyun chhod gaye raste pe tanha humein...\nKaisi yeh saza di hai wafa ki tune?",
                verse1 = "Kagaz ke phoolon mein khushboo kahan thi,\nHumne toh dhoondi thi chhaon teri.\nBheegi hui aankhon se dekha jo aasmaan,\nToot gaya jaise koi aashiyaan.",
                preChorus = "Dard hi dard hai seene mein chupa,\nAb kisse karein hum yeh gila...",
                chorus = "Toota jo dil toh aawaz na aayi,\nHar mod pe bas mili hai tanhai.\nKyun bewajah tu humse juda ho gaya,\nMera khuda hi mujhse khafa ho gaya!",
                verse2 = "Tasveer teri abhi bhi sambhali hai,\nHar yaad mein ek aag jalaali hai.\nRaatein guzar jaati hain taaron ko ginte,\nKash tu milta humein phir se chalte.",
                bridge = "Zakhmon ko marham ab lagta nahi,\nKoi bhi apna ab dikhta nahi...",
                finalChorus = "Toota jo dil toh aawaz na aayi,\nHar mod pe bas mili hai tanhai!\nKyun bewajah tu humse juda ho gaya,\nMera khuda hi mujhse khafa ho gaya!",
                outro = "Alvida mere humdum...\nAlvida...\n(Soft sorrowful piano fading)"
            )
            else -> LyricsStructure(
                title = title,
                language = LyricsLanguage.HINDI,
                mood = mood,
                intro = "(High-energy 808 drop & synthesizer sweep)\nYeah! Mintu Raj AI in the zone!\nAaja dikhate hain asman ka rang!",
                verse1 = "Subah ki dhoop se shaam ki roshni tak,\nApna hi raaj chalega yahan sab.\nSapnon ko pankh diye mehnat ki aag ne,\nRok na sakega koi humein raaste pe.",
                preChorus = "Beat badhao, shor machao,\nApni kahani khud tum banao!",
                chorus = "Hawa mein udaan hai, apna jahaan hai,\nHar dil mein dharakta yeh armaan hai!\nNa darenge na jhukenge kabhi bhi,\nYeh toh bas shuruaat ka elaan hai!",
                verse2 = "Bheed mein pehchaan humne banayi,\nApne hi dam pe manzil sajayi.\nBass jab baje toh zameen bhi hilegi,\nKismat ki chaabi ab humein hi milegi.",
                bridge = "Aandhi ho ya toofan, hum hain tayaar,\nJeet ke aayenge har ek baar!",
                finalChorus = "Hawa mein udaan hai, apna jahaan hai,\nHar dil mein dharakta yeh armaan hai!\nNa darenge na jhukenge kabhi bhi,\nYeh toh bas shuruaat ka elaan hai!",
                outro = "We keep moving up!\nMintu Raj AI Music! Power mode off!"
            )
        }
    }

    private fun generateBhojpuriLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return LyricsStructure(
            title = title,
            language = LyricsLanguage.BHOJPURI,
            mood = mood,
            intro = "(Lively Dholak + Harmonium peppy riff)\nArre haan bhai haan!\nBhojpuriya rhythm baajela re!\nAe kareja, sun la aawaz!",
            verse1 = "Gori tohar jhumka ba gazab ke hilawala,\nLalka chunariya mein chaand muskaye.\nGaon ke galiyan mein dhum machawala,\nDekh ke tohar roop dil bag-bag ho jaye.",
            preChorus = "Nazar jo milawalu ta jaadu chalawalu,\nDilwa ke chainva tohari le urawalu...",
            chorus = "Ae rani tohar ankhiya ke kajarawa,\nMaar dale karejwa pe bijuriya re!\nNachela manwa jhum-jhum ke,\nJab bajela dholak dhum-dhum ke!",
            verse2 = "Chham-chham payaliya tohar bole,\nBhojpuriya dhun pe sara jahaan dole.\nKhali tohra sanghe jeeye ke ba sapna,\nTuhi ta banalu ab jindgi apan.",
            bridge = "Koyal jaisan tohar bol batiya,\nRang dehalu tuhi mor saari ratiya!",
            finalChorus = "Ae rani tohar ankhiya ke kajarawa,\nMaar dale karejwa pe bijuriya re!\nNachela manwa jhum-jhum ke,\nJab bajela dholak dhum-dhum ke!",
            outro = "Jiya ho Bihar ke lala!\nMintu Raj AI Music rock karela!"
        )
    }

    private fun generateBhaktiLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return LyricsStructure(
            title = title,
            language = LyricsLanguage.BHAKTI,
            mood = SongMood.SPIRITUAL,
            intro = "(Shankha naad with meditative tanpura & temple bells)\nOm Namah Shivaya...\nHar Har Mahadev...\nKarpur Gauram Karunavataram...",
            verse1 = "Bhasma ramaye gale mund maala,\nTrishul dharak he deen dayala.\nJata mein Ganga kare kal-kal dhara,\nSharan tihaari aayo jag saara.",
            preChorus = "Dam-dam damru baje re bhole,\nRom-rom mein shiv naam dole...",
            chorus = "Jai Ho Bholenath Dayalu,\nKaaton sab bhakton ke kasht karalu!\nHey Shambhu Mahadev Shankara,\nKripa karo he dukh-bhanjana!",
            verse2 = "Chhath Ghate suruj devta viraje,\nArghya arpan kare sab subah saje.\nKashi Vishwanath Kailash Pati,\nTumhare charan mein sharan shanti.",
            bridge = "Bhakti bhaav se dhyan lagaye,\nHar sankat se mukti paaye...",
            finalChorus = "Jai Ho Bholenath Dayalu,\nKaaton sab bhakton ke kasht karalu!\nHey Shambhu Mahadev Shankara,\nKripa karo he dukh-bhanjana!",
            outro = "Om Shanti Shanti Shanti...\nHar Har Mahadev..."
        )
    }

    private fun generatePunjabiLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return LyricsStructure(
            title = title,
            language = LyricsLanguage.PUNJABI,
            mood = mood,
            intro = "(Punchy Tumbi roll + heavy Dhol beat)\nOye nachne nu kar le tayaari!\nPunjabiyan di shaan nirali!\nChak de phatte!",
            verse1 = "Kudiye ni tere nakhre hazaar,\nMunda tere piche gaddi lai ke tyaar.\nSuit patiala tera karda kamaal,\nBhangre di beat utte nach saare naal.",
            preChorus = "Dhol utte jado dandi vajdi,\nSaari duniya he dekh nachdi...",
            chorus = "Ni tere utte dil aa gaya,\nDesi jatt te roop chhaa gaya!\nBass poori loud karke,\nDesi gaana floor utte laa gaya!",
            verse2 = "Kurte pajama vich taur puri kaim,\nBairiyan da kadd ditta sara asi veham.\nYaaran di yaari te garoor karde,\nRab kolon bas sachiyan duawan mangde.",
            bridge = "Sheran wangu rehnde kade darde nahi,\nVaada karke kade piche mud-de nahi!",
            finalChorus = "Ni tere utte dil aa gaya,\nDesi jatt te roop chhaa gaya!\nBass poori loud karke,\nDesi gaana floor utte laa gaya!",
            outro = "Mintu Raj beats, Desi vibe all night!"
        )
    }

    private fun generateTamilLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return LyricsStructure(
            title = title,
            language = LyricsLanguage.TAMIL,
            mood = mood,
            intro = "(Nadhaswaram flourishes with energetic Thavil rhythm)\nVaada vaada nanba!\nTamil beat adhiruthu paaru!\nReady ah?",
            verse1 = "Kaatril vandha unnodaya kavidhai,\nNenjukkul ezhudhiya pudhiya aasaigal.\nVizhigal rendum unnai thaane thedum,\nNizhalai pola unpinne thodarum.",
            preChorus = "Thudi thudikkum idhayam unnai paarthu,\nKavalaigal marandhom paadal kettu...",
            chorus = "Kanmani un anbu dhaan venum,\nKaalam muzhudhum un kooda vazha thonum!\nThavilum melamum kalandhu paada,\nIdhayam thudikuthu aattam poda!",
            verse2 = "Sirikkum un sirippil amudham kandein,\nNal vazhi kaattum deepam aanein.\nUlagam muzhudhum paadum nam paattu,\nUnmai anbukku illai oru pootu.",
            bridge = "Vinnai thaandi sellum namma isai,\nEndrum kuraiyaadhu indha aasai!",
            finalChorus = "Kanmani un anbu dhaan venum,\nKaalam muzhudhum un kooda vazha thonum!\nThavilum melamum kalandhu paada,\nIdhayam thudikuthu aattam poda!",
            outro = "Namma isai, namma sandhosham!\nMintu Raj AI Music!"
        )
    }

    private fun generateEnglishLyrics(title: String, mood: SongMood, prompt: String): LyricsStructure {
        return LyricsStructure(
            title = title,
            language = LyricsLanguage.ENGLISH,
            mood = mood,
            intro = "(Glitchy synth arpeggios rising through a low-pass filter)\nYeah, breathing the neon air tonight...\nEchoes vibrating in the dark...\nHere we go.",
            verse1 = "Walking down avenues washed in rain,\nReflecting city lights through the pane.\nEvery heartbeat is a countdown clock,\nWe're breaking out of every digital lock.",
            preChorus = "Can you feel the frequency rising now?\nNo looking back, we're crossing the wire...",
            chorus = "We are the sound of the future alive,\nThrough the static and shadow we thrive!\nTurn the volume up high, let it burn,\nIt's our moment, it's finally our turn!",
            verse2 = "Lines of code turning into symphonies,\nUnchained melodies drifting on the breeze.\nI found my compass inside the beat,\nNow the midnight world is under our feet.",
            bridge = "No boundaries left to hold us down,\nWe wear the starlight like a crown!",
            finalChorus = "We are the sound of the future alive,\nThrough the static and shadow we thrive!\nTurn the volume up high, let it burn,\nIt's our moment, it's finally our turn!",
            outro = "Fading into electric skies...\nYour imagination, your music...\nMintu Raj AI."
        )
    }

    fun rewriteLine(currentLine: String, mood: SongMood): String {
        val alternatives = when (mood) {
            SongMood.ROMANTIC -> listOf(
                "Har dhadkan mein bas tera hi fitoor hai",
                "Teri aankhon mein simat gaya hai sara aasmaan",
                "Tere bina adhoora har ek lamha ho gaya",
                "Chhu loon hawa ban ke teri mehekti saansein"
            )
            SongMood.SAD -> listOf(
                "Ashqon ki boondein ab ginti nahi karti",
                "Raahon mein bikhre hain yaadon ke kaanch",
                "Kyun juda huye jab jeene ka sabab tum the",
                "Tanhaiyon ne gher liya hai shaam hote hi"
            )
            SongMood.SPIRITUAL -> listOf(
                "Charanon mein shish jhuka kar manga vardan",
                "Bhakti ka deep jale har ek man mandir mein",
                "Hey Deenbandhu taar do sansar se humein",
                "Omkar ki dhwani se goonj utha brahmand"
            )
            else -> listOf(
                "Aag lagayenge nayi raahon pe chal ke",
                "Manzilen khud aayengi kadmon ko choomne",
                "Duniya dekhegi apna naya andaz",
                "Suraj ki tarah chamkega apna kal"
            )
        }
        return alternatives.random()
    }
}
