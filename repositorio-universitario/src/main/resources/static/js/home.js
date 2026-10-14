paginaAtual = 0;
    let disciplinaAtual = null;
    document.addEventListener("DOMContentLoaded", () =>{
        listarArquivos(0);
        listarArquivosPorDisciplina();
    });

    async function listarArquivos(pagina){
        disciplinaAtual = null;
        paginaAtual = pagina;
        try{
            const response = await fetch(`/arquivos?page=${pagina}`, {
                method: 'GET',
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            const data =  await response.json();
            renderArquivos(data);
        }catch (error){
            console.log("Falha em listar arquivos do repositório", error);
        }
    }


    function listarArquivosPorDisciplina(){
        document.querySelectorAll("#lista-disciplinas ul li").forEach(icon =>{
            icon.addEventListener('click', async () =>{
                const nomeDisciplina = icon.textContent.trim();
                disciplinaAtual = nomeDisciplina;
                paginaAtual = 0;
                try {
                    const response = await fetch(`/arquivos/${encodeURIComponent(disciplinaAtual)}?page=0`,{
                        method: 'GET',
                        credentials: "include"
                    });
                    if(!response.ok){
                        throw new Error(`Erro HTTP: ${response.status}`);
                    }
                    const data = await response.json();
                    renderArquivos(data);
                }catch (error){
                    console.log(`Falha em listar arquivos de ${nomeDisciplina}`, error);
                }
            })
        })
    }

    async function listarArquivosPorDisciplinaPagina(pagina){
        if(!disciplinaAtual) return;
        paginaAtual = pagina;
        try {
            const response = await fetch(`/arquivos/${encodeURIComponent(disciplinaAtual)}?page=${pagina}`,{
                method: 'GET',
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            const data = await response.json();
            renderArquivos(data);
        }catch (error){
            console.log(`Falha em listar arquivos`, error);
        }
    }

    function renderArquivos(data){
        let info = '';

        if (!data.content || data.content.length === 0) {
            info = `
                <tr>
                    <td colspan="5" style="text-align: center; padding: 20px; color: #666;">
                        Nenhum arquivo disponibilizado no momento. Mude isso contribuindo com o repositório!
                    </td>
                </tr>
            `;
        }
        else{
            data.content.forEach(function (arquivo){
                const dataArquivo = arquivo.data
                        ? new Date(arquivo.data).toLocaleString("pt-BR")
                        : "-";
                
                const temDescricao = arquivo.descricao && arquivo.descricao.trim() !== "";
                const descricaoTexto = temDescricao ? arquivo.descricao : "Sem descrição fornecida.";

                info += `
                    <tr class="linha-arquivo" onclick="alternarDescricao(${arquivo.id})" style="cursor: pointer;">
                        <td>
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <span class="material-symbols-outlined icone-seta" id="seta-${arquivo.id}" style="font-size: 18px; color: #666; transition: transform 0.2s;">
                                    chevron_right
                                </span>
                                <span>${arquivo.nome}</span>
                            </div>
                        </td>
                        <td>${arquivo.disciplina}</td>
                        <td>${dataArquivo}</td>
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined botao-visualizar" data-id="${arquivo.id}" title="Visualizar">visibility</span>
                        </td>
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined botao-download" data-id="${arquivo.id}" data-nome="${arquivo.nome_real_arquivo}" title="Download">download</span>
                        </td>
                    </tr>
                    <tr id="descricao-${arquivo.id}" class="linha-descricao" style="display: none; background-color: #f9fbfd;">
                        <td colspan="5" style="padding: 12px 24px; border-bottom: 1px solid #e0e0e0;">
                            <p style="margin: 0; color: #4a5568; font-size: 0.9em; line-height: 1.4;">
                                <strong style="color: #2d3748;">Descrição:</strong> ${descricaoTexto}
                            </p>
                        </td>
                    </tr>
                `;
            });
        }
        document.getElementById("lista-arquivos").innerHTML = info;
        downloadArquivo();
        visualizarArquivo();
        let botoesPagina = '';
        for(let i = 0; i < data.totalPages; i++){
            if(disciplinaAtual){
                botoesPagina += `<button onclick="listarArquivosPorDisciplinaPagina(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i+1}</button>`
            }else{
                botoesPagina += `<button onclick="listarArquivos(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i+1}</button>`
            }

        }
        document.getElementById("paginacao-arquivos").innerHTML = botoesPagina;
    }

function fazerUploadArquivo(){
    const upload = document.getElementById("confirmar");
    upload.addEventListener("click", async () =>{
        try{
            let arquivo = document.getElementById("arquivo");
            let nomeArquivo = document.getElementById("nome-arquivo");
            let disciplina = document.getElementById("disciplina");
            let descricao = document.getElementById("descricao-arquivo");

            const formData = new FormData;
            formData.append("file", arquivo.files[0]);
            formData.append("nome", nomeArquivo.value);
            formData.append("disciplina", disciplina.value);
            formData.append("descricao", descricao.value);
            const response = await fetch(`/upload`, {
               method: 'POST',
                credentials: "include",
                body: formData
            });
            console.log(response.status);
            if(response.status === 401){
                document.getElementById("overlay").style.display = "none";
                mostrarNotificacao("alert")
               
            }
            else if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            else {
                arquivo.value = '';
                nomeArquivo.value = '';
                disciplina.value = '';
                descricao.value = '';
                document.getElementById("overlay").style.display = "none";
                mostrarNotificacao("success")
              
            }

        }catch (error){
            mostrarNotificacao("error")
            console.error("Erro em realizar upload de arquivo", error);
        }
    });
}

fazerUploadArquivo();

function mostrarNotificacao(type) {
    const notificacao = document.querySelector(".notificacao");
    if(type==="success"){
        notificacao.textContent = "Arquivo enviado para avaliação";
    }
    else if(type==="error"){
        notificacao.textContent = "Erro em realizar upload de arquivo";
    }
    else if(type==="alert"){
        notificacao.textContent = "Não é possível realizar upload. Motivo: sua conta está suspensa"
    }

    notificacao.className = `notificacao ${type}`;
    notificacao.style.display = "block";

    setTimeout(() => {
        notificacao.style.display = "none";
    }, 3000);
}