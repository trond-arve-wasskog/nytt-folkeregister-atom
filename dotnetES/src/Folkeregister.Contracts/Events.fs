namespace Folkeregister.Contracts.Events
open Folkeregister.Contracts.Types
open Folkeregister.Infrastructure
open System

type PersonCreated = {Id: Guid; SSN: SSN; Name: Name} 
    with interface IEvent with member this.Id with get() = this.Id

type AddressAdded = {Id: Guid; Address: Address}
    with interface IEvent with member this.Id with get() = this.Id
//type OrderCreated = 
//    {Id: Guid; UserId: Guid; ProductId: Guid; Quantity: int}
//    with interface IEvent
