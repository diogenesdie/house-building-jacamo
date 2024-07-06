// Esta empresa licita para a tarefa de Painting
// Estratégia: diminui o lance atual em 5 unidades, desde que não seja inferior ao preço mínimo

{ include("common.asl") }

min_price(1000). // preço mínimo para a tarefa de Painting

/* Planos para fase de leilão */

!discover_art("auction_for_Painting").

+currentBid(V)[artifact_id(Art)]      // há um novo valor para o lance atual
    : not i_am_winning(Art) &         // Eu não sou o vencedor
      min_price(P) &
      task(S)[artifact_id(Art)] &
      V > P                           // Ainda posso oferecer um lance melhor
   <- //.print("Meu lance no artefato ", Art, ", Task ", S,", é ",math.max(V-5,P));
      bid( math.max(V-5,P) )[ artifact_id(Art) ].  // faço meu lance oferecendo um serviço mais barato

/* planos para fase de execução */

{ include("org_code.asl") }
{ include("org_goals.asl") }
