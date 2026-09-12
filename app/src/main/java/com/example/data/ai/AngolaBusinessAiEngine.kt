package com.example.data.ai

import com.example.data.local.entity.BusinessProfileEntity
import kotlinx.coroutines.delay

object AngolaBusinessAiEngine {

    private const val BASE_ANGOLA_SYSTEM_INSTRUCTION = """
Você é o assistente oficial de inteligência artificial da plataforma "Negócio IA Kz", especializado em consultoria empresarial, redação comercial, marketing digital, vendas e estratégia para o mercado de Angola.
Diretrizes fundamentais:
- Responda sempre em português claro, profissional, persuasivo e empático.
- Contextualize para Angola: use a moeda Kwanza (Kz), refira dinâmicas do comércio angolano (vendas pelo WhatsApp, entregas em Luanda e províncias, pagamentos via Multicaixa Express ou Transferência Bancária).
- Seja prático e direto, com foco em resultados rápidos e estratégias de baixo custo para pequenas e médias empresas, comerciantes e freelancers.
- Não invente dados falsos e formate as respostas com títulos claros, marcadores e emojis bem dosados.
"""

    suspend fun generateContent(
        toolType: String,
        profile: BusinessProfileEntity?,
        inputs: Map<String, String>,
        customApiKey: String? = null
    ): String {
        val bizName = profile?.businessName ?: inputs["businessName"] ?: "O Seu Negócio"
        val bizType = profile?.businessType ?: inputs["businessType"] ?: "Comércio / Serviços"
        val location = if (!profile?.province.isNullOrBlank()) "${profile.province} - ${profile.municipality}" else (inputs["location"] ?: "Angola")
        val targetAudience = profile?.targetAudience ?: inputs["targetAudience"] ?: "Consumidores e empresas locais"

        val promptDetails = buildPromptForTool(toolType, bizName, bizType, location, targetAudience, inputs)

        // Try Live Gemini first
        val geminiResult = GeminiApiClient.callGemini(
            systemPrompt = BASE_ANGOLA_SYSTEM_INSTRUCTION,
            userPrompt = promptDetails,
            customApiKey = customApiKey
        )

        if (geminiResult.isSuccess && !geminiResult.getOrNull().isNullOrBlank()) {
            return geminiResult.getOrNull()!!
        }

        // Graceful Intelligent Engine Fallback
        delay(600) // Brief natural pause for processing experience
        return generateLocalSpecializedContent(toolType, bizName, bizType, location, targetAudience, inputs)
    }

    private fun buildPromptForTool(
        toolType: String,
        bizName: String,
        bizType: String,
        location: String,
        audience: String,
        inputs: Map<String, String>
    ): String {
        return when (toolType) {
            "NAME_GEN" -> """
Gere 10 nomes inovadores e memoráveis para um negócio em Angola:
- Setor: $bizType (${inputs["category"] ?: ""})
- Público-alvo: $audience
- Estilo: ${inputs["style"] ?: "Moderno e Profissional"}
- Localização: $location
- Palavras-chave: ${inputs["keywords"] ?: "Qualidade, confiança"}
Para cada nome forneça:
1. Nome
2. Significado
3. Slogan sugerido
4. Posicionamento de marca
"""
            "SLOGAN_GEN" -> """
Crie 8 opções de slogans para a empresa "$bizName" ($bizType em $location):
- Estilo: ${inputs["style"] ?: "Profissional e Moderno"}
- Público: $audience
- Diferencial: ${inputs["highlight"] ?: "Qualidade e bom atendimento"}
Apresente cada slogan com uma breve explicação do seu impacto no consumidor angolano.
"""
            "POST_FB" -> """
Crie um post comercial persuasivo para o Facebook para o negócio "$bizName" ($bizType em $location):
- Produto/Serviço: ${inputs["product"] ?: "Oferta especial"}
- Objetivo: ${inputs["goal"] ?: "Gerar vendas"}
- Promoção: ${inputs["promo"] ?: "Preço competitivo em Kz"}
- Tom: ${inputs["tone"] ?: "Amigável e Persuasivo"}
- Call to Action: ${inputs["cta"] ?: "Faça já a sua encomenda pelo WhatsApp"}
Forneça:
1. Título chamativo
2. Texto principal com gancho (hook) e benefícios
3. Chamada para ação (CTA) com WhatsApp
4. Hashtags recomendadas para Angola (ex: #Luanda #Angola #VendasLuanda)
"""
            "INSTA_POST" -> """
Crie um conteúdo completo para o Instagram para "$bizName" ($bizType em $location):
- Formato: ${inputs["format"] ?: "Carrossel / Post"}
- Tema: ${inputs["theme"] ?: "Novidades da semana"}
- Tom: ${inputs["tone"] ?: "Profissional e Moderno"}
Forneça:
1. Gancho nos primeiros 3 segundos / Slide 1
2. Conteúdo/Legenda completa
3. Chamada para Ação (CTA)
4. Sugestão visual para foto/vídeo
5. 10 Hashtags estratégicas para Angola
"""
            "AD_CAMPAIGN" -> """
Crie 4 versões de anúncio para campanhas pagas ou status WhatsApp/Facebook/Instagram para "$bizName":
- Produto: ${inputs["product"] ?: "Produto em destaque"}
- Preço: ${inputs["price"] ?: "Consulte o preço em Kz"}
- Público: $audience
- Localização: $location
Gere claramente:
1. Versão Curta (Ideal para WhatsApp Status / Reels)
2. Versão Persuasiva (Foco em benefícios e solução)
3. Versão Profissional (Foco em credibilidade e garantia)
4. Versão Urgente (Gatilho de escassez e promoção por tempo limitado)
"""
            "PROD_DESC" -> """
Crie uma descrição completa e persuasiva para o produto:
- Nome do Produto: ${inputs["product"] ?: "Item"}
- Preço: ${inputs["price"] ?: "Kz"}
- Características: ${inputs["features"] ?: "Alta qualidade"}
- Benefícios: ${inputs["benefits"] ?: "Praticidade e elegância"}
Gere:
1. Descrição Curta
2. Descrição Completa e persuasiva
3. Lista de Benefícios
4. Texto pronto para WhatsApp
5. Texto pronto para Instagram
6. Texto pronto para Facebook
"""
            "CLIENT_REPLY" -> """
Atue como atendente comercial da empresa "$bizName" ($bizType em $location).
O cliente enviou a seguinte mensagem:
"${inputs["message"] ?: "Olá, quanto custa e como posso pagar?"}"
Intenção percebida: ${inputs["intent"] ?: "Pedido de preço / Dúvidas"}
Tom escolhido: ${inputs["tone"] ?: "Amigável e Profissional"}
Gere uma resposta educada, acolhedora e vendedora, explicando o processo de pagamento em Angola (Multicaixa Express ou transferência) e incentivando o fecho da compra.
"""
            "MKT_PLAN" -> """
Crie um Plano de Marketing prático de ${inputs["duration"] ?: "30"} dias para "$bizName" ($bizType em $location):
- Objetivo: ${inputs["goal"] ?: "Aumentar faturamento em Kz"}
- Orçamento estimado: ${inputs["budget"] ?: "Baixo custo / Orgânico"}
- Público: $audience
Estruture o plano com:
1. Objetivos e Metas
2. Estratégias por semana
3. Calendário de publicações e canais (WhatsApp, Instagram, Facebook)
4. Ações promocionais e parcerias
5. Indicadores de sucesso (KPIs)
"""
            "CONTENT_IDEAS" -> """
Crie uma lista de 15 ideias criativas de conteúdo para redes sociais da empresa "$bizName" ($bizType em $location).
Para cada ideia inclua: Tema, Formato (Reels/Post/Story), Gancho (Hook) e Objetivo.
"""
            "SALES_ANALYSIS" -> """
Analise os dados de vendas da empresa "$bizName" em $location:
${inputs["salesSummary"] ?: "Total de vendas: 1.250.000 Kz"}
Forneça:
1. Resumo financeiro do desempenho
2. Pontos fortes identificados
3. Riscos ou gargalos operacionais
4. Recomendações imediatas para aumentar a margem de lucro e atrair mais clientes angolanos
"""
            else -> """
Ajude a empresa "$bizName" ($bizType em $location) com a seguinte solicitação:
${inputs["prompt"] ?: "Sugestões de melhoria para o negócio"}
"""
        }
    }

    private fun generateLocalSpecializedContent(
        toolType: String,
        bizName: String,
        bizType: String,
        location: String,
        audience: String,
        inputs: Map<String, String>
    ): String {
        val phone = inputs["phone"] ?: "9XX XXX XXX"
        val prod = inputs["product"] ?: "Nossos Produtos & Serviços"
        val price = inputs["price"] ?: "Melhor preço em Kz"

        return when (toolType) {
            "NAME_GEN" -> """
💡 10 Sugestões de Nomes para $bizType em $location

1. KwanzaPrime $bizType
• Significado: Evoca liderança, solidez e excelência financeira nacional.
• Slogan: A sua escolha de confiança em Angola.
• Posicionamento: Marca premium e confiável.

2. LuandaMais Soluções
• Significado: Foco em crescimento dinâmico e proximidade com o mercado local.
• Slogan: Sempre mais por você e pela sua família.
• Posicionamento: Acessível, moderno e rápido.

3. Kizomba Commerce & Style
• Significado: Ritmo, harmonia e conexão com a cultura e alegria angolana.
• Slogan: O seu estilo no ritmo certo.
• Posicionamento: Jovem, vibrante e autêntico.

4. Imbondeiro Negócios
• Significado: Inspirado na árvore sagrada angolana — força, longevidade e raízes firmes.
• Slogan: Raízes fortes, grandes resultados.
• Posicionamento: Institucional, robusto e duradouro.

5. Muxima Delivery & Shop
• Significado: "Muxima" (Coração em Kimbundo) — dedicação feita com alma.
• Slogan: Feito de coração para a sua vida.
• Posicionamento: Atendimento caloroso e personalizado.

6. Kilamba Express
• Significado: Agilidade e modernidade urbana nas principais centralidades.
• Slogan: Rapidez e qualidade à sua porta.
• Posicionamento: Praticidade e conveniência do dia a dia.

7. BenguelaVibe
• Significado: Energia positiva, litoral e inovação comercial.
• Slogan: A essência do melhor para si.
• Posicionamento: Descontraído e sofisticado.

8. Estrela Dourada
• Significado: Brilho, valorização e destaque no mercado.
• Slogan: Você merece o que brilha mais.
• Posicionamento: Exclusivo e de alto padrão.

9. Ponto Certo Angola
• Significado: O destino exato onde o cliente encontra o que procura sem enrolação.
• Slogan: A certeza da melhor compra.
• Posicionamento: Direto, econômico e transparente.

10. NovaGeração IA & Comércio
• Significado: Visão futurista e tecnológica aplicada aos negócios angolanos.
• Slogan: O futuro do seu negócio começa aqui.
• Posicionamento: Inovador e pioneiro.
""".trimIndent()

            "SLOGAN_GEN" -> """
🎯 Slogans Estratégicos para "$bizName" ($bizType)

Opção 1 (Profissional & Confiável):
"Qualidade que você confia, excelência que Angola merece."

Opção 2 (Moderno & Ágil):
"O seu dia a dia mais simples, rápido e inteligente."

Opção 3 (Foco em Valor & Kwanza):
"O melhor investimento para o seu bolso e para o seu negócio."

Opção 4 (Emocional & Angolano):
"Cuidamos do que é seu com todo o coração."

Opção 5 (Liderança & Prestígio):
"A referência certa quando você busca o melhor em $location."

Opção 6 (Jovem & Dinâmico):
"Mais estilo, mais presença, mais você."

Opção 7 (Praticidade no WhatsApp):
"A um clique de distância no seu WhatsApp com entrega rápida."

Opção 8 (Premium):
"Sofisticação e padrão internacional com a autenticidade de Angola."
""".trimIndent()

            "POST_FB" -> """
📢 POST DE ALTO IMPACTO PARA FACEBOOK

📌 TÍTULO:
🔥 ATENÇÃO $location: Descubra como $prod vai transformar o seu dia!

📝 TEXTO PRINCIPAL:
Você já sentiu que está na hora de dar um passo à frente com produtos que realmente entregam qualidade sem surpresas desagradáveis?

Na $bizName, selecionamos o que há de melhor para você:
✅ Padrão de qualidade rigoroso e garantia real
✅ Atendimento personalizado e direto no WhatsApp
✅ Preços justos em Kwanza ($price)
✅ Entregas rápidas em Luanda e envio seguro para as províncias

💡 Não deixe para depois! As nossas unidades são limitadas e a procura tem sido enorme esta semana.

📲 COMO ENCOMENDAR:
É muito fácil: envie agora uma mensagem para o nosso WhatsApp através do número $phone ou clique no link da bio.
Aceitamos pagamentos práticos via Multicaixa Express e Transferência Bancária.

#Angola #Luanda #NegociosAngola #VendasOnlineAngola #CompreEmAngola #Kwanza #EmpreendedorismoAngola
""".trimIndent()

            "INSTA_POST" -> """
📸 ROTEIRO & LEGENDA PARA INSTAGRAM

🎯 FORMATO: Carrossel de 3 Slides / Reels de 30s
🎵 ÁUDIO SUGERIDO: Música instrumental moderna em alta

🎬 ESTRUTURA DO CONTEÚDO:
• Slide 1 / Primeiros 3s (Gancho): "O maior segredo para quem procura $prod de verdade em $location..."
• Slide 2 (Problema vs Solução): "Muitas pessoas perdem tempo e dinheiro com opções sem garantia. Aqui na $bizName nós garantimos durabilidade e suporte."
• Slide 3 (Oferta Especial): "Preço especial desta semana em Kz com entrega rápida!"

✍️ LEGENDA PRONTA PARA COPIAR:
Procurando qualidade e segurança para o seu dia a dia? Na $bizName facilitamos tudo para si! 🇦🇴✨

Comente "EU QUERO" ou clique no link da nossa biografia para falar diretamente conosco pelo WhatsApp ($phone).

📦 Entregas disponíveis em toda Luanda e despachos rápidos para outras províncias!
💳 Pagamentos via Multicaixa Express facilitado.

#Luanda #Angola #EmpreenderAngola #Qualidade #WhatsAppSales #ModaAngola #TecnologiaAngola
""".trimIndent()

            "AD_CAMPAIGN" -> """
🚀 CAMPANHA DE ANÚNCIOS ($bizName)

1️⃣ VERSÃO CURTA (Status WhatsApp & TikTok)
🔥 Super Oferta em $location! $prod com preço imperdível ($price).
⚡ Peça já pelo WhatsApp: $phone. Entregas rápidas! 🇦🇴

2️⃣ VERSÃO PERSUASIVA (Facebook & Instagram Feed)
👉 Cansado de pagar caro por pouca qualidade? Na $bizName unimos o melhor custo-benefício de Angola com atendimento nota 10.
Adquira hoje o seu $prod e pague com segurança via Multicaixa Express.
💬 Envie mensagem agora para o WhatsApp $phone e garanta a sua unidade!

3️⃣ VERSÃO PROFISSIONAL (Parcerias & B2B)
A $bizName oferece soluções completas em $bizType para clientes exigentes em $location. Conte com pontualidade, transparência e fatura proforma rápida.
📞 Contacte a nossa equipa: $phone.

4️⃣ VERSÃO URGENTE (Promoção Relâmpago)
🚨 ÚLTIMAS UNIDADES! Desconto especial válido apenas até este fim de semana para $prod.
Garanta já a sua encomenda antes que esgote o stock. Clique no botão e fale no WhatsApp: $phone!
""".trimIndent()

            "PROD_DESC" -> """
📦 FICHA DESCRITIVA COMERCIAL: $prod

💰 Preço: $price
🏢 Empresa: $bizName ($location)

🔹 Descrição Curta (Para Catálogo ou Bio):
O $prod da $bizName combina durabilidade, acabamento premium e o melhor custo-benefício para quem não abre mão de qualidade.

🔹 Descrição Completa e Persuasiva:
Se você procura excelência em $location, o $prod foi pensado exatamente nas necessidades do consumidor angolano. Com materiais selecionados e desempenho comprovado, ele proporciona praticidade e satisfação garantida desde o primeiro uso.

🔹 Principais Vantagens & Benefícios:
• Alto rendimento e longa vida útil
• Design moderno e ergonômico
• Assistência e acompanhamento pós-venda em Angola
• Preço competitivo em Kwanza com opções de pagamento flexíveis

📱 TEXTO PRONTO PARA WHATSAPP:
"Olá! Seguem os detalhes do *$prod*:
💵 Preço: $price
📍 Retirada ou Entrega rápida em Luanda/Províncias
💳 Pagamento: Multicaixa Express / Transferência
Deseja reservar a sua unidade agora?"
""".trimIndent()

            "CLIENT_REPLY" -> """
💬 RESPOSTA COMERCIAL PROFISSIONAL PARA CLIENTE

Saudação calorosa e resposta estratégica:

"Olá, estimado(a) cliente! Tudo bem consigo? 👋
Muito obrigado pelo seu contacto com a *$bizName*!

Com muito gosto informamos que temos sim disponibilidade imediata do *$prod*.
O valor é de *$price*.

💳 Para sua comodidade, facilitamos o pagamento via:
• Multicaixa Express (rápido e sem taxas extras)
• Transferência Bancária (com envio do comprovativo)
• Dinheiro no ato (para levantamentos)

🛵 Fazemos entregas em Luanda com toda a segurança e enviamos também para todas as províncias de Angola.

Posso já solicitar os seus dados para preparar a sua encomenda?"
""".trimIndent()

            "MKT_PLAN" -> """
📅 PLANO DE MARKETING DE 30 DIAS PARA $bizName

🎯 OBJETIVO: Aumentar as vendas de $prod e elevar o faturamento mensal em Kwanza.
📍 PRAÇA: $location | 📱 CANAIS PRINCIPAIS: WhatsApp Business, Instagram e Facebook.

SEMANA 1: Ativação & Apresentação
• Segunda: Post educativo sobre as dores que $prod resolve.
• Quarta: Vídeo curto nos Stories mostrando os bastidores e o stock real em Angola.
• Sexta: Campanha de Status no WhatsApp com chamada para os primeiros 10 clientes.

SEMANA 2: Prova Social & Autoridade
• Terça: Depoimento ou foto de cliente satisfeito recebendo o produto em Luanda.
• Quinta: Reels com dica prática sobre $bizType.
• Sábado: Lembrete de final de semana com botão direto para o WhatsApp.

SEMANA 3: Promoção & Escassez
• Segunda: Anúncio da "Semana do Cliente" com brinde ou entrega facilitada.
• Quarta: Transmissão no WhatsApp para clientes antigos oferecendo desconto exclusivo.
• Sexta: Alerta de últimas unidades em stock.

SEMANA 4: Fechamento & Retenção
• Terça: Comparativo de valor — Por que comprar na $bizName é o melhor investimento.
• Quinta: Antecipação de novidades para o próximo mês.
• Sábado: Mensagem de agradecimento e pesquisa rápida de satisfação.

📊 KPIs de Sucesso:
• Mínimo de 30 novos contactos no WhatsApp por semana
• Conversão de 20% dos orçamentos enviados
• Crescimento sustentável da margem líquida em Kz
""".trimIndent()

            "CONTENT_IDEAS" -> """
💡 15 IDEIAS DE CONTEÚDO PARA AS REDES SOCIAIS ($bizName)

1. Reels: "3 erros que você comete ao escolher $prod em Angola." (Formato: Vídeo de 25s)
2. Post Estático: "Antes e Depois: A transformação que nossos clientes experimentam."
3. Story Interativo: "Enquete: Você prefere pagar via Multicaixa Express ou Transferência?"
4. Carrossel: "Guia definitivo para fazer o seu Kwanza render mais em $bizType."
5. Reels: "Como embalamos os pedidos com todo o cuidado aqui em $location."
6. Post: "O produto estrela da semana que todo mundo está pedindo no WhatsApp."
7. Story: "Caixinha de perguntas: Tire qualquer dúvida sobre os nossos prazos de entrega."
8. Reels Cômico/Humor leve: "A felicidade de receber o pacote da $bizName na sexta-feira."
9. Post Dica: "5 cuidados essenciais para o seu $prod durar o dobro do tempo."
10. Carrossel: "Conheça quem faz acontecer: A história por trás da nossa empresa."
11. Story: "Alerta de reposição de stock! Quem reservou primeiro já está a receber."
12. Reels: "Testando na prática: Veja a qualidade deste material ao vivo."
13. Post Motivacional: "Orgulho de empreender e servir Angola todos os dias."
14. Story: "Tutorial rápido de como fazer seu pedido no WhatsApp em 2 passos."
15. Post Promocional: "Combo especial do mês para você economizar em Kz."
""".trimIndent()

            "SALES_ANALYSIS" -> """
📊 ANÁLISE INTELIGENTE DE VENDAS & ESTRATÉGIA ($bizName)

📌 1. Diagnóstico Financeiro
• O fluxo de vendas demonstra tração no mercado de $location, com boa receptividade dos produtos principais.
• O ticket médio pode ser elevado através de combos e vendas cruzadas (cross-selling).

📌 2. Pontos Fortes
• Pagamentos facilitados via Multicaixa Express aumentam a taxa de conversão imediata.
• Proximidade com o cliente via WhatsApp cria fidelização e recompras recorrentes.

📌 3. Oportunidades de Melhoria
• Reduzir o tempo de resposta aos pedidos de orçamento para evitar que o cliente compre no concorrente.
• Manter registo diário rigoroso de custos de transporte e taxas bancárias para proteger o lucro líquido em Kz.

📌 4. Recomendações Estratégicas para o Mercado Angolano:
1. Lance a "Garantia de Entrega Rápida" — promessa clara de entrega em 24h para Luanda.
2. Crie um programa simples de fidelidade: "Na 5ª compra ganhe 10% de desconto".
3. Aumente o foco nos fins de semana e dias 25 a 30 do mês (período de salários).
""".trimIndent()

            else -> """
✨ Consultoria Empresarial IA para $bizName ($location)

Com base nas características do mercado de Angola, aqui estão as principais recomendações estratégicas:
1. Agilidade Comercial: Mais de 80% das vendas no varejo angolano fecham através do WhatsApp. Treine o atendimento rápido.
2. Formas de Pagamento: Facilite pagamentos por Multicaixa Express e forneça sempre referências claras.
3. Transparência de Preço: Clientes valorizam orçamentos discriminados em Kz sem custos ocultos.
4. Parcerias Locais: Colabore com motoboys e empresas de logística locais para baratear o custo do frete para o cliente.
""".trimIndent()
        }
    }
}
